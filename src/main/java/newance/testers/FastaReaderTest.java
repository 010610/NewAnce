/*
Copyright (C) SIB - Swiss Institute of Bioinformatics, Lausanne, Switzerland
Copyright (C) LICR - Ludwig Institute of Cancer Research, Lausanne, Switzerland
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
*/

package newance.testers;

import newance.proteinmatch.*;
import newance.util.ExecutableOptions;
import newance.util.NewAnceParams;
import newance.util.RunTime2String;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * @author Markus Müller
 */

public class FastaReaderTest extends ExecutableOptions {

    protected String fastaFile;
    protected NewAnceParams params;
    protected int maxNrDisplayedPSMs;
    protected String fileType;

    public FastaReaderTest() {

        createOptions();
        fileType = "uniprot";
        maxNrDisplayedPSMs = -1;
    }

    public static void main(String[] args) {

        FastaReaderTest fastaReaderTest =  new FastaReaderTest();
        try {
            fastaReaderTest.init(args).parseOptions(args).run();
        } catch (MissingOptionException e) {
            fastaReaderTest.checkHelpOption(args, "-h");
            fastaReaderTest.checkVersionOption(args, NewAnceParams.getInstance().getVersion(), "-v");
            fastaReaderTest.printOptions(args,e.getMessage());
        } catch (ParseException e) {
            fastaReaderTest.printOptions(args, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FastaReaderTest init() throws IOException {

        return this;
    }

    public int run() throws IOException {

        long start = System.currentTimeMillis();

        FastaDB fastaDB;
        if (fileType.equals("uniprot")) fastaDB = new UniProtDB(fastaFile);
        else fastaDB = new VariantProtDB(fastaFile);

        System.out.println("Reading fasta in " + RunTime2String.getTimeDiffString(System.currentTimeMillis() - start));
        int cnt = 0;

        System.out.println("\nNewAnceID\tProteinID\tProtName\tGeneName\tDescription\tDB\tSequence");

        for (FastaProtein prot : fastaDB.getProteins()) {
            if(maxNrDisplayedPSMs>=0 && cnt>=maxNrDisplayedPSMs) break;

            if (fileType.equals("uniprot")) {
                UniProtProtein protein = (UniProtProtein) prot;
                System.out.println(protein.toString() + "\t" + prot.getProteinID() + "\t" + protein.getUniProtName() + "\t" +
                        protein.getGeneName() + "\t" + protein.getDescription() + "\t" + protein.getDbFlag());
                System.out.println(prot.getSequence());
            } else {
                VariantProtein protein = (VariantProtein) prot;
                System.out.println(protein.toString());
                System.out.println(prot.getSequence());
            }
            cnt++;
        }

        return 0;
    }


    protected void createOptions() {

        this.cmdLineOpts = new Options();

        cmdLineOpts.addOption(Option.builder("t").required(false).hasArg(true).longOpt("fileType").desc("Fasta file type to be tested (uniprot or peff)").build());
        cmdLineOpts.addOption(Option.builder("fa").required(false).hasArg().longOpt("fastaFile").desc("Fasta file to be tested").build());
        cmdLineOpts.addOption(Option.builder("maxP").required(false).hasArg().longOpt("maxDisplayedPsms").desc("Maximal number of psms written to standard output").build());
        cmdLineOpts.addOption(Option.builder("h").required(false).hasArg(false).longOpt("help").desc("Help option for command line help").build());
        cmdLineOpts.addOption(Option.builder("v").required(false).hasArg(false).longOpt("version").desc("Version of NewAnce software").build());
    }

    @Override
    protected void check(CommandLine line) throws ParseException {

        this.params = NewAnceParams.getInstance();

        String maxPStr = getOptionString(line,"maxP");
        maxNrDisplayedPSMs = (maxPStr.isEmpty())?-1:Integer.parseInt(maxPStr);

        fastaFile = getOptionString(line,"fa");
        fileType = getOptionString(line, "t").toLowerCase();
        if (fileType.isEmpty()) fileType = "uniprot";
    }

}
