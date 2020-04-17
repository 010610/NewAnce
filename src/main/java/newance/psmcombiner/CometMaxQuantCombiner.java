/*
Copyright (C) SIB - Swiss Institute of Bioinformatics, Lausanne, Switzerland
Copyright (C) LICR - Ludwig Institute of Cancer Research, Lausanne, Switzerland
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
*/

package newance.psmcombiner;

import newance.psmconverter.PeptideSpectrumMatch;
import newance.util.*;
import org.apache.commons.cli.*;
import newance.proteinmatch.OccamRazorSpectrumCounter;
import newance.proteinmatch.UniProtDB;
import newance.psmconverter.MaxQuantMultipleMSMSFileConverter;
import newance.psmconverter.CometMultiplePepXMLFileConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Markus Müller
 */

public class CometMaxQuantCombiner extends ExecutableOptions {

    protected PsmGrouper psmGrouper;
    protected NewAnceParams params;
    protected SpectrumAccumulator spectrumAccumulator;


    public CometMaxQuantCombiner() {

        params = NewAnceParams.getInstance();
        version = params.getVersion();

        createOptions();
        spectrumAccumulator = new SpectrumAccumulator();
    }

    public static void main(String[] args) {

        CometMaxQuantCombiner cometScoreCombiner =  new CometMaxQuantCombiner();
        try {
            cometScoreCombiner.init(args).parseOptions(args).run();
        } catch (MissingOptionException e) {
        } catch (ParseException e) {
            cometScoreCombiner.printOptions(args, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int run() throws IOException {

        long start;

        UniProtDB uniProtDB = null;
        if (!params.getUniprotFastaFile().isEmpty()) {
            start = System.currentTimeMillis();
            System.out.println("Load UniProt sequences from "+params.getUniprotFastaFile()+" ...");
            uniProtDB = new UniProtDB(params.getUniprotFastaFile());
            System.out.println("Loading UniProt sequences ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));
        }

        System.out.println("Parsing Comet pep.xml files ...");
        GroupedFDRCalculator groupedFDRCalculator = buildGroupedFDRCalculator(uniProtDB);

        CometMultiplePepXMLFileConverter cometMultiplePepXMLConverter = new CometMultiplePepXMLFileConverter(params.getCometPsmDir(), params.getCometPsmRegExp(), groupedFDRCalculator, false);
        cometMultiplePepXMLConverter.run();

        processGroupedFDRCalculator(groupedFDRCalculator); // calculate local fdr here

        MaxQuantMultipleMSMSFileConverter maxQuantMultipleMSMSConverter = null;
        if (params.isIncludeMaxQuant()) {
            maxQuantMultipleMSMSConverter = new MaxQuantMultipleMSMSFileConverter(params.getMaxquantPsmDir(), params.getMaxquantPsmRegExp());
            maxQuantMultipleMSMSConverter.run();

            System.out.println("Adding UniProt annotations from "+params.getUniprotFastaFile()+" to MaxQuant PSMs ...");
            start = System.currentTimeMillis();
            maxQuantMultipleMSMSConverter.addDBProteins(uniProtDB);
            System.out.println("MaxQuant DB matching ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));
        }

        start = System.currentTimeMillis();

        if (params.getFdrControlMethod().equals("combined")) {
            controlFDRCombined(groupedFDRCalculator, cometMultiplePepXMLConverter, maxQuantMultipleMSMSConverter);
        } else {
            controlFDRSeparate(groupedFDRCalculator, cometMultiplePepXMLConverter, maxQuantMultipleMSMSConverter);
        }
        System.out.println("Grouped global FDR calculation ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));

        if (params.isWriteParamsFile()) writeParams();

        start = System.currentTimeMillis();
        writePeptideProteinGroupReport(uniProtDB);
        System.out.println("Protein grouping ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));

        return 0;
    }

    protected GroupedFDRCalculator buildGroupedFDRCalculator(UniProtDB uniProtDB) {

        if (params.getCodingProtRegExp()!=null) {
            if (params.getForcedNoncanonicalProts().isEmpty())
                psmGrouper = new RegExpProteinGrouper(params.getCodingProtRegExp(), params.getProtCodingGroup(), params.getNoncanonicalGroup());
            else
                psmGrouper = new RegExpProteinGrouper(params.getCodingProtRegExp(), params.getForcedNoncanonicalProts(), params.getProtCodingGroup(), params.getNoncanonicalGroup());
        } else {
            psmGrouper = new ModificationPSMGrouper();
        }

        GroupedFDRCalculator groupedFDRCalculator = new GroupedFDRCalculator(psmGrouper, uniProtDB);

        return groupedFDRCalculator;
    }

    protected GroupedFDRCalculator processGroupedFDRCalculator(GroupedFDRCalculator groupedFDRCalculator ) {

        groupedFDRCalculator.setCanCalculateFDR(params.getMinNrPsmsPerHisto());
        groupedFDRCalculator.importPriorHistos();
        groupedFDRCalculator.calcClassProbs();
        groupedFDRCalculator.calcLocalFDR();
        groupedFDRCalculator.smoothHistogram(params.getSmoothDegree());
        groupedFDRCalculator.calcLocalFDR();
        if (params.isReportHistos()) groupedFDRCalculator.writeHistograms(params.getOutputDir()+File.separator+"histos", params.getOutputTag());

        return groupedFDRCalculator;
    }

    protected void controlFDRCombined(GroupedFDRCalculator groupedFDRCalculator, CometMultiplePepXMLFileConverter cometMultiplePepXMLConverter, MaxQuantMultipleMSMSFileConverter maxQuantMultipleMSMSConverter) {

        float lFDRThreshold = groupedFDRCalculator.calcLocalFDRThreshold((float)params.getFdrCometThreshold());


        writeGroupHistoTree(groupedFDRCalculator.printTree(lFDRThreshold));
//        test(cometMultiplePepXMLConverter.getPsms(),groupedFDRCalculator,lFDRThreshold);

        SummaryReportWriter summaryReportWriter = new SummaryReportWriter(params.getOutputDir() +File.separator+params.getOutputTag()+"_SummaryReport.txt", params.isIncludeMaxQuant());

        for (String group : groupedFDRCalculator.getGroups()) {

            long start = System.currentTimeMillis();
            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> filteredCometPsms = groupedFDRCalculator.filterPsms(cometMultiplePepXMLConverter.getPsms(), lFDRThreshold, group);
            System.out.println("Comet FDR filtering ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));


//            test(filteredCometPsms,groupedFDRCalculator,lFDRThreshold,group);

            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> combined;
            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> maxQuantPsms = null;
            if (params.isIncludeMaxQuant()) {
                maxQuantPsms = ProcessPsmUtils.extractGroupPsms(psmGrouper, maxQuantMultipleMSMSConverter.getPsms(),group);

                start = System.currentTimeMillis();
                combined = combine(filteredCometPsms,maxQuantPsms);
                System.out.println("Comet-MaxQuant combiner ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));

            } else {
                combined = filteredCometPsms;
            }

            combined.forEach(10000,spectrumAccumulator);

            writeToCombTabFile(combined, groupedFDRCalculator, group+"_"+params.getOutputTag()+"_NewAncePSMs.txt");
            System.out.println(combined.size()+" spectra combined for group " + group);

            if (params.isIncludeMaxQuant()) {
                summaryReportWriter.write(group, combined, filteredCometPsms, maxQuantPsms);
            } else {
                summaryReportWriter.write(group, combined);
            }

            System.out.println("Write data to summary report file.");
        }

        summaryReportWriter.close();
    }

    protected void controlFDRSeparate(GroupedFDRCalculator groupedFDRCalculator, CometMultiplePepXMLFileConverter cometMultiplePepXMLConverter, MaxQuantMultipleMSMSFileConverter maxQuantMultipleMSMSConverter) {

        Map<String, Float> grpThresholdMap = groupedFDRCalculator.calcGroupLocalFDRThreshold((float)params.getFdrCometThreshold());

        System.out.print(groupedFDRCalculator.printTree(grpThresholdMap));

        SummaryReportWriter summaryReportWriter = new SummaryReportWriter(params.getOutputDir() +File.separator+params.getOutputTag()+"_SummaryReport.txt", params.isIncludeMaxQuant());

        for (String group : groupedFDRCalculator.getGroups()) {

            long start = System.currentTimeMillis();
            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> filteredCometPsms = groupedFDRCalculator.filterPsms(cometMultiplePepXMLConverter.getPsms(), grpThresholdMap.get(group), group);
            System.out.println("Comet FDR filtering ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));

            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> combined;
            ConcurrentHashMap<String, List<PeptideSpectrumMatch>> maxQuantPsms = null;
            if (params.isIncludeMaxQuant()) {

                maxQuantPsms = ProcessPsmUtils.extractGroupPsms(psmGrouper, maxQuantMultipleMSMSConverter.getPsms(),group);

                start = System.currentTimeMillis();
                combined = combine(filteredCometPsms,maxQuantPsms);
                System.out.println("Comet-MaxQuant combiner ran in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));

            } else {
                combined = filteredCometPsms;
            }

            combined.forEach(10000,spectrumAccumulator);

            writeToCombTabFile(combined, groupedFDRCalculator, group+"_"+params.getOutputTag()+"_NewAncePSMs.txt");
            System.out.println(combined.size()+" spectra combined for group " + group);

            if (params.isIncludeMaxQuant()) {
                summaryReportWriter.write(group, combined, filteredCometPsms, maxQuantPsms);
            } else {
                summaryReportWriter.write(group, combined);
            }

            System.out.println("Write data to summary report file.");
        }

        summaryReportWriter.close();
    }

    protected void test(ConcurrentHashMap<String, List<PeptideSpectrumMatch>>  cometPsms, GroupedFDRCalculator groupedFDRCalculator, float lFDRThreshold) {

        ConcurrentHashMap<String, List<PeptideSpectrumMatch>>  filtered = groupedFDRCalculator.filterPsms(cometPsms, lFDRThreshold);

        int tCnt = 0, dCnt = 0;
        for (String specID : filtered.keySet()) {
            for (PeptideSpectrumMatch psm : filtered.get(specID)) {
                if (psm.isDecoy()) dCnt++;
                else tCnt++;
            }
        }

        float[] counts = groupedFDRCalculator.getTargetDecoyCounts(lFDRThreshold);

        System.out.println("root: test Psm count. tCnt= "+tCnt+"/"+counts[1]+", dCnt= "+dCnt+"/"+counts[0]);
    }

    protected void test(ConcurrentHashMap<String, List<PeptideSpectrumMatch>>  filtered, GroupedFDRCalculator groupedFDRCalculator, float lFDRThreshold, String group) {

        int tCnt = 0, dCnt = 0;
        for (String specID : filtered.keySet()) {
            for (PeptideSpectrumMatch psm : filtered.get(specID)) {
                if (psm.isDecoy()) dCnt++;
                else tCnt++;
            }
        }

        float[] counts = groupedFDRCalculator.getTargetDecoyCounts(lFDRThreshold,group);

        System.out.println(group+": test Psm count. tCnt= "+tCnt+"/"+counts[1]+", dCnt= "+dCnt+"/"+counts[0]);
    }

    protected void writeGroupHistoTree(String grpStatisticsInfo) {

        String paramsFileName = params.getOutputDir() + File.separator + params.getOutputTag()+"_NewAnceStatistics.txt";

        try {
            BufferedWriter paramsWriter = new BufferedWriter(new FileWriter(new File(paramsFileName)));
            paramsWriter.write(grpStatisticsInfo);
            paramsWriter.close();
        } catch (IOException e) {
            System.out.println("Cannot write NewAnce parameters to file "+paramsFileName+".");
        }
    }

    protected ConcurrentHashMap<String, List<PeptideSpectrumMatch>> combine(ConcurrentHashMap<String, List<PeptideSpectrumMatch>>  cometPsms, ConcurrentHashMap<String, List<PeptideSpectrumMatch>> maxQuantPsms) {

        ConcurrentHashMap<String, List<PeptideSpectrumMatch>> combined = new ConcurrentHashMap<>();

        CometMaxQuantPsmMerger combiner = new CometMaxQuantPsmMerger(maxQuantPsms,combined);
        cometPsms.forEach(combiner);

        return combined;
    }

    protected void writeToCombTabFile(ConcurrentHashMap<String, List<PeptideSpectrumMatch>>  psms, GroupedFDRCalculator groupedFDRCalculator,
                                      String filename)  {

        final Psm2StringFunction stringFunction = new Psm2StringFunction(Psm2StringFunction.TabStringMode.COMBINED, groupedFDRCalculator);

        StringFileWriter writer = new StringFileWriter(params.getOutputDir() + File.separator +filename, stringFunction);

        psms.forEach(10000, stringFunction, writer);

        writer.close();
    }


    protected void writePeptideProteinGroupReport(UniProtDB uniProtDB) {

        if (!params.isDoPeptideProteinGrouping()) return;

        if (params.getSearchFastaFile() !=null) uniProtDB.addFastaFile(params.getSearchFastaFile());
        OccamRazorSpectrumCounter spectrumCounter = new OccamRazorSpectrumCounter(spectrumAccumulator, uniProtDB);

        String reportFileName = params.getOutputDir() + File.separator + params.getOutputTag()+"_PeptideProteinGroupingReport.txt";
        try {
            spectrumCounter.write(new File(reportFileName));
        } catch (IOException e) {
            System.out.println("Cannot writeHistograms protein group report to file "+reportFileName+".");
        }
    }

    protected void writeParams() {

        String paramsFileName = params.getOutputDir() + File.separator + params.getOutputTag()+"_NewAnceParameters.txt";

        try {
            BufferedWriter paramsWriter = new BufferedWriter(new FileWriter(new File(paramsFileName)));
            paramsWriter.write(params.toString());
            paramsWriter.close();
        } catch (IOException e) {
            System.out.println("Cannot write NewAnce parameters to file "+paramsFileName+".");
        }
    }

    protected void createOptions() {

        this.cmdLineOpts = new Options();

        cmdLineOpts.addOption(Option.builder("coD").required().hasArg().longOpt("cometPsmDir").desc("Comet psm root directory (required)").build());
        cmdLineOpts.addOption(Option.builder("mqD").required(false).hasArg().longOpt("maxquantPsmDir").desc("MaxQuant psm root directory. If not provided only Comet is used.").hasArg().build());
        cmdLineOpts.addOption(Option.builder("coRE").required().hasArg().longOpt("cometPsmRegex").desc("Regular expression of Comet psm files (e.g. \\.xml$) (required)").build());
        cmdLineOpts.addOption(Option.builder("coFDR").required().hasArg().longOpt("cometFDR").desc("FDR for filtering Comet PSMs before combination (required) (default value 0.03)").build());
        cmdLineOpts.addOption(Option.builder("outD").required().hasArg().longOpt("outputDir").desc("Output directory for results (required)").build());
        cmdLineOpts.addOption(Option.builder("repH").required(false).hasArg(false).longOpt("reportHistogram").desc("Report histograms to text files").build());
        cmdLineOpts.addOption(Option.builder("readH").required(false).hasArg().longOpt("readHistograms").desc("Directory where histograms files are placed.").build());
        cmdLineOpts.addOption(Option.builder("fH").required(false).hasArg(false).longOpt("forceHistograms").desc("Histograms are imported even if enough PSMs are available.").build());
        cmdLineOpts.addOption(Option.builder("groupM").required(false).hasArg().longOpt("groupingMethod").desc("Method for PSM grouping: fasta or modif or none (default none).").build());
        cmdLineOpts.addOption(Option.builder("groupN").required(false).hasArg().longOpt("groupNames").desc("Comma separated list of names of sequence groups in fasta file (e.g. prot,lncRNA,TE ). Will be used as prefixes for output files.").build());
        cmdLineOpts.addOption(Option.builder("groupRE").required(false).hasArg().longOpt("groupRegEx").desc("Comma separated list of regular expression defining sequence groups of fasta headers (e.g. \"sp\\||tr\\|ENSP00\",\"ENST00\",\"SINE_|LINE_|LTR_|DNA_|Retroposon_\" ). Will be used as prefixes for output files.").build());
        cmdLineOpts.addOption(Option.builder("protG").required(false).hasArg().longOpt("proteinGroup").desc("Name of group with protein coding or canonical sequences (default \"prot\"). Will be used as prefix for output files.").build());
        cmdLineOpts.addOption(Option.builder("noncG").required(false).hasArg().longOpt("noncanonicalGroup").desc("Name of group with non-canonical or cryptic sequences (default \"nonc\"). Will be used as prefix for output files.").build());
        cmdLineOpts.addOption(Option.builder("protRE").required(false).hasArg().longOpt("protRegExp").desc("Regular expression to match fasta name of coding proteins (e.g. sp\\||tr\\| ).").build());
        cmdLineOpts.addOption(Option.builder("spRE").required(false).hasArg().longOpt("spectrumFilter").desc("If this option is set, only spectrum ids that match this regexp are used.  If not set no filtering is performed.").build());
        cmdLineOpts.addOption(Option.builder("exclP").required(false).hasArg().longOpt("excludeProts").desc("Regular expression of proteins excluded from analysis. If not set no proteins are excluded.").build());
        cmdLineOpts.addOption(Option.builder("noncP").required(false).hasArg().longOpt("noncanonicalProts").desc("Comma separated list of protein names to be included in noncanonical group even if they are in UniProt (e.g. PGBD5_HUMAN,POGZ_HUMAN,PGBD1_HUMAN)").build());
        cmdLineOpts.addOption(Option.builder("mod").required(false).hasArg().longOpt("modifications").desc("Comma separated list of peptide modifications used in search (e.g. Cysteinyl:C3H5NO2S,Oxidation:O)").build());
        cmdLineOpts.addOption(Option.builder("seFa").required(false).hasArg().longOpt("searchFastaFile").desc("Fasta file that was used for the search (required for protein grouping export)").build());
        cmdLineOpts.addOption(Option.builder("upFa").required(false).hasArg().longOpt("uniProtFastaFile").desc("Fasta file with coding or canonical proteins (e.g. UniProt fasta file)").build());
        cmdLineOpts.addOption(Option.builder("ppG").required(false).hasArg(false).longOpt("peptideProteinGrouping").desc("Perform peptide protein grouping export.").build());
        cmdLineOpts.addOption(Option.builder("maxR").required(false).hasArg().longOpt("maxRank").desc("Maximal rank of peptide in list of spectrum matches (rank 1 = best) (default value: 1)").build());
        cmdLineOpts.addOption(Option.builder("minZ").required(false).hasArg().longOpt("minCharge").desc("Minimal charge of PSM (default value: 1)").build());
        cmdLineOpts.addOption(Option.builder("maxZ").required(false).hasArg().longOpt("maxCharge").desc("Maximal charge of PSM (default value: 5)").build());
        cmdLineOpts.addOption(Option.builder("minL").required(false).hasArg().longOpt("minLength").desc("Minimal length of peptide (default value: 8)").build());
        cmdLineOpts.addOption(Option.builder("maxL").required(false).hasArg().longOpt("maxLength").desc("Maximal length of peptide (default value: 25)").build());
        cmdLineOpts.addOption(Option.builder("nrTh").required(false).hasArg().longOpt("nrThreads").desc("Number of threads used by NewAnce (default value: nr of available processors - 2)").build());
        cmdLineOpts.addOption(Option.builder("smD").required(false).hasArg().longOpt("smoothDegree").desc("Degree of smoothing (0: no smoothing, n: n x smoothing) (default value 1)").build());
        cmdLineOpts.addOption(Option.builder("outT").required(false).hasArg().longOpt("outputTag").desc("Tag inserted into output file names after prefix.").build());
        cmdLineOpts.addOption(Option.builder("minPH").required(false).hasArg().longOpt("minPsm4Histo").desc("Minimal number of psms to calculate local FDR in histogram (default value: 100000).").build());
        cmdLineOpts.addOption(Option.builder("fdrM").required(false).hasArg().longOpt("fdrControlMethod").desc("Method to control pFDR: combined or separate (default combined).").build());
        cmdLineOpts.addOption(Option.builder("minXC").required(false).hasArg().longOpt("minXCorr").desc("Minimal Comet XCorr in histogram (default value 0)").build());
        cmdLineOpts.addOption(Option.builder("maxXC").required(false).hasArg().longOpt("maxXCorr").desc("Maximal Comet XCorr in histogram (default value 5)").build());
        cmdLineOpts.addOption(Option.builder("nrXCB").required(false).hasArg().longOpt("nrXCorrBins").desc("Number of Comet XCorr bins in histogram (default value 40)" ).build());
        cmdLineOpts.addOption(Option.builder("minSP").required(false).hasArg().longOpt("minSpScore").desc("Minimal Comet SpScore in histogram (default value 0)").build());
        cmdLineOpts.addOption(Option.builder("maxSP").required(false).hasArg().longOpt("maxSpScore").desc("Maximal Comet SpScore in histogram (default value 1)").build());
        cmdLineOpts.addOption(Option.builder("nrSPB").required(false).hasArg().longOpt("nrSpScoreBins").desc("Number of Comet SpScore bins in histogram (default value 40)").build());
        cmdLineOpts.addOption(Option.builder("minDC").required(false).hasArg().longOpt("minDeltaCn").desc("Minimal Comet DeltaCn in histogram (default value 0)").build());
        cmdLineOpts.addOption(Option.builder("maxDC").required(false).hasArg().longOpt("maxDeltaCn").desc("Maximal Comet DeltaCn in histogram (default value 2500)").build());
        cmdLineOpts.addOption(Option.builder("nrDCB").required(false).hasArg().longOpt("nrDeltaCnBins").desc("Number of Comet DeltaCn bins in histogram (default value 40)").build());
        cmdLineOpts.addOption(Option.builder("wP").required(false).hasArg(false).longOpt("write2ParamFile").desc("This option is set if parameters should be written to file.").build());
        cmdLineOpts.addOption(Option.builder("rP").required(false).hasArg().longOpt("readParamFile").desc("Name of file from which parameters should to read.").build());
        cmdLineOpts.addOption(Option.builder("d").required(false).hasArg(false).longOpt("debug").desc("Debug option").build());
        cmdLineOpts.addOption(Option.builder("h").required(false).hasArg(false).longOpt("help").desc("Help option for command line help").build());
        cmdLineOpts.addOption(Option.builder("v").required(false).hasArg(false).longOpt("version").desc("Version of NewAnce software").build());
    }

    @Override
    protected void check(CommandLine line) throws ParseException {

        if (optionsSet) return;

        this.params = NewAnceParams.getInstance();

        params.add("cometPsmDir", getOptionString(line, "coD"));
        params.add("cometPsmRegExp", getOptionString(line, "coRE"));

        String mqDir = getOptionString(line, "mqD");
        params.add("includeMaxQuant", mqDir.isEmpty()?"false":"true");
        params.add("maxquantPsmDir", getOptionString(line, "mqD"));

        params.add("debug", getOptionString(line, "d"));
        params.add("forceHistos", getOptionString(line, "fH"));
        params.add("reportHistos", getOptionString(line, "repH"));
        params.add("readHistos", getOptionString(line, "readH"));
        params.add("outputDir", getOptionString(line, "outD"));
        params.add("searchFastaFile", getOptionString(line, "seFa"));
        params.add("uniprotFastaFile", getOptionString(line, "upFa"));
        params.add("doPeptideProteinGrouping", getOptionString(line, "ppG"));
        params.add("writeParamsFile", getOptionString(line, "wP"));
        params.add("readParamsFile", getOptionString(line, "rP"));
        params.add("maxRank", getOptionString(line, "maxR"));
        params.add("minCharge", getOptionString(line, "minZ"));
        params.add("maxCharge", getOptionString(line, "maxZ"));
        params.add("minPeptideLength", getOptionString(line, "minL"));
        params.add("maxPeptideLength", getOptionString(line, "maxL"));
        params.add("fdrCometThreshold", getOptionString(line, "coFDR"));
        params.add("protCodingGroup", getOptionString(line, "protG"));
        params.add("noncanonicalGroup", getOptionString(line, "noncG"));
        params.add("excludedProtPattern", getOptionString(line, "exclP"));
        params.add("forcedNoncanonicalProts", getOptionString(line, "noncP"));
        params.add("spectrumRegExp", getOptionString(line, "spRE"));
        params.add("codingProtRegExp", getOptionString(line, "protRE"));
        params.add("outputTag", getOptionString(line, "outT"));
        params.add("modifications", getOptionString(line, "mod"));
        params.add("minNrPsmsPerHisto", getOptionString(line, "minPH"));
        params.add("minXCorr", getOptionString(line, "minXC"));
        params.add("maxXCorr", getOptionString(line, "maxXC"));
        params.add("nrXCorrBins", getOptionString(line, "nrXCB"));
        params.add("minDeltaCn", getOptionString(line, "minDC"));
        params.add("maxDeltaCn", getOptionString(line, "maxDC"));
        params.add("nrDeltaCnBins", getOptionString(line, "nrDCB"));
        params.add("minSpScore", getOptionString(line, "minSP"));
        params.add("maxSpScore", getOptionString(line, "maxSP"));
        params.add("nrSpScoreBins", getOptionString(line, "nrSPB"));
        params.add("nrThreads", getOptionString(line, "nrTh"));
        params.add("smoothDegree", getOptionString(line, "smD"));
        params.add("fdrControlMethod", getOptionString(line, "fdrM"));
        params.add("groupNames", getOptionString(line, "groupN"));
        params.add("groupRegExs", getOptionString(line, "groupRE"));

        params.finalize();

    }
}
