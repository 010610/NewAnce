/*
Copyright (C) SIB - Swiss Institute of Bioinformatics, Lausanne, Switzerland
Copyright (C) LICR - Ludwig Institute of Cancer Research, Lausanne, Switzerland
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
*/


package newance.psmconverter;

import newance.util.PsmPredicate;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.proteomics.mol.modification.Modification;
import org.expasy.mzjava.proteomics.ms.ident.ModListModMatchResolver;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author Markus Müller
 */

public class CometPepXmlConverter extends SinglePsmFileConverter {

    public CometPepXmlConverter(File msmsFile, Map<String,List<PeptideSpectrumMatch>> psms, CountDownLatch latch) {

        super(msmsFile, psms, latch);
    }
    @Override
    public void run() {

        System.out.println("Reading " + psmFile);

        final Map<String, List<PeptideSpectrumMatch>> psmMap = new HashMap<>();

        PsmPredicate psmPredicate = new PsmPredicate(params.getMinCharge(), params.getMaxCharge(), params.getMinPeptideLength(), params.getMaxPeptideLength(), params.getMaxRank(),
                params.getCometMainScore(), params.getCometMainScoreMinValue(), PsmPredicate.ScoreOrder.LARGER);

        PeptideSpectrumMatchList peptideSpectrumMatchList = new PeptideSpectrumMatchList(new SpectrumKeyFunctionImpl(), psmPredicate, psmMap);

        Collection<Modification> modifications = params.getModifications();
        ModListModMatchResolver modMatchResolver = new ModListModMatchResolver(new AbsoluteTolerance(params.getModifMatchMassTol()), modifications);

        CometPEFFPepXmlReader psmReader = new CometPEFFPepXmlReader( true, modMatchResolver);
        psmReader.parse(psmFile, peptideSpectrumMatchList);

        addPsms(psmMap);

        if (latch!=null) latch.countDown();
        System.out.println("Finished reading " + psmFile+". Latch count: "+((latch!=null)?latch.getCount():-1));
    }
}
