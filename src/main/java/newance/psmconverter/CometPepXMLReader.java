package newance.psmconverter;

import com.google.common.base.Optional;
import org.expasy.mzjava.core.ms.spectrum.RetentionTime;
import org.expasy.mzjava.proteomics.io.ms.ident.PSMReaderCallback;
import org.expasy.mzjava.proteomics.io.ms.ident.PepXmlReader;
import org.expasy.mzjava.proteomics.io.ms.ident.pepxml.v117.MsmsPipelineAnalysis;
import org.expasy.mzjava.proteomics.io.ms.ident.pepxml.v117.NameValueType;
import org.expasy.mzjava.proteomics.mol.AminoAcid;
import org.expasy.mzjava.proteomics.mol.modification.ModAttachment;
import org.expasy.mzjava.proteomics.ms.ident.*;

import java.util.List;

/**
 * Copyright (C) 2019
 * @author Markus Müller
 * @Institutions: SIB, Swiss Institute of Bioinformatics; Ludwig Institute for Cancer Research
 */

public class CometPepXMLReader extends PepXmlReader {
    public CometPepXMLReader(ModMassStorage modMassStorage, boolean discardAmbiguousSequences) {
        super(modMassStorage, discardAmbiguousSequences);
    }

    public CometPepXMLReader(ModMassStorage modMassStorage, boolean discardAmbiguousSequences, ModificationMatchResolver modMatchResolver) {
        super(modMassStorage, discardAmbiguousSequences, modMatchResolver);
    }

    protected void processSearchHit(PSMReaderCallback callback, SpectrumIdentifier identifier, MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit searchHit) {

        String peptideSequence = searchHit.getPeptide();

        if(discardAmbiguous && containsUnknownAA(peptideSequence))
            return;

        PeptideMatch peptideMatch = new PeptideMatch(peptideSequence);
        peptideMatch.setRank(Optional.fromNullable((int) searchHit.getHitRank()));

        Integer numMatchedIons = (searchHit.getNumMatchedIons() != null) ? searchHit.getNumMatchedIons().intValue() : null;
        peptideMatch.setNumMatchedIons(Optional.fromNullable(numMatchedIons));
        Integer totNumIons = (searchHit.getTotNumIons() != null) ? searchHit.getTotNumIons().intValue() : null;
        peptideMatch.setTotalNumIons(Optional.fromNullable(totNumIons));
        peptideMatch.setMassDiff(Optional.fromNullable(parseDouble(searchHit.getMassdiff())));
        Integer numMissedCleavages = (searchHit.getNumMissedCleavages() != null) ? searchHit.getNumMissedCleavages().intValue() : null;
        peptideMatch.setNumMissedCleavages(Optional.fromNullable(numMissedCleavages));
        peptideMatch.setRejected(Optional.fromNullable(parseBoolean(searchHit.getIsRejected())));

        MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo modInfo = searchHit.getModificationInfo();
        boolean isVariant = copyModVarInfo(peptideMatch, modInfo);


        if (isVariant)
            peptideMatch.addProteinMatch(new PeptideProteinMatch("variant__"+searchHit.getProtein(), Optional.<String>absent(),
                    Optional.fromNullable(searchHit.getPeptidePrevAa()), Optional.fromNullable(searchHit.getPeptideNextAa()), PeptideProteinMatch.HitType.UNKNOWN));
        else
            peptideMatch.addProteinMatch(new PeptideProteinMatch(searchHit.getProtein(), Optional.<String>absent(),
                    Optional.fromNullable(searchHit.getPeptidePrevAa()), Optional.fromNullable(searchHit.getPeptideNextAa()), PeptideProteinMatch.HitType.UNKNOWN));

        for (MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AlternativeProtein protein : searchHit.getAlternativeProtein()) {
            if (isVariant)
                peptideMatch.addProteinMatch(new PeptideProteinMatch("variant__"+protein.getProtein(), Optional.<String>absent(),
                        Optional.fromNullable(protein.getPeptidePrevAa()), Optional.fromNullable(protein.getPeptideNextAa()), PeptideProteinMatch.HitType.UNKNOWN));
            else
                peptideMatch.addProteinMatch(new PeptideProteinMatch(searchHit.getProtein(), Optional.<String>absent(),
                        Optional.fromNullable(searchHit.getPeptidePrevAa()), Optional.fromNullable(searchHit.getPeptideNextAa()), PeptideProteinMatch.HitType.UNKNOWN));
        }

        for (NameValueType searchScore : searchHit.getSearchScore()) {

            final String name = searchScore.getName();
            final double score = parseDouble(searchScore.getValueAttribute());
            peptideMatch.addScore(name, score);

            if (name.equals("expect")) {
                double negLogP;
                if (score>10e-50)
                    negLogP = -Math.log10(score);
                else
                    negLogP =  -Math.log10(10e-50);

                peptideMatch.addScore("neg_log10_p",negLogP);
            }
        }

        addRetentionTime(peptideMatch,identifier);
        readAnalysisResult(searchHit, peptideMatch);
        callback.resultRead(identifier, peptideMatch);
    }

    protected boolean copyModVarInfo(PeptideMatch peptideMatch, MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo modInfo) {

        if (modInfo == null) return false;

        if (modInfo.getModAminoacidMass() != null ) {
            for (MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass modAaMass : modInfo.getModAminoacidMass()) {

                int position = modAaMass.getPosition().intValue() - 1;
                AminoAcid residue = peptideMatch.getSymbol(position);
                ModificationMatch modMatch = peptideMatch.addModificationMatch(position, adjustMass(modAaMass.getMass(), residue));
                resolveMod(modMatch);
            }

            if (modInfo.getModAminoacidMass().isEmpty()) {
                return true;
            }
        }
        if (modInfo.getModNtermMass() != null) {

            ModificationMatch modMatch = peptideMatch.addModificationMatch(ModAttachment.N_TERM, adjustMass(modInfo.getModNtermMass(), ModAttachment.N_TERM));
            resolveMod(modMatch);
        }
        if (modInfo.getModCtermMass() != null) {

            ModificationMatch modMatch = peptideMatch.addModificationMatch(ModAttachment.C_TERM, adjustMass(modInfo.getModCtermMass(), ModAttachment.C_TERM));
            resolveMod(modMatch);
        }

        return false;
    }

    protected void addRetentionTime(PeptideMatch peptideMatch, SpectrumIdentifier identifier) {

        List<RetentionTime> rts = identifier.getRetentionTimes();
        if (rts.isEmpty())
            peptideMatch.addScore("rt", -1.0);
        else
            peptideMatch.addScore("rt", identifier.getRetentionTimes().getFirst().getTime());

    }

}
