/**
 * Copyright (C) 2019, SIB/LICR. All rights reserved
 *
 * SIB, Swiss Institute of Bioinformatics
 * Ludwig Institute for Cancer Research (LICR)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/LICR nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/LICR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package newance.psmcombiner;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.expasy.mzjava.proteomics.mol.Peptide;
import newance.psmconverter.PeptideMatchData;
import newance.util.NewAnceParams;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Markus Müller
 */

public class CometMaxQuantPsmCombinerTest {

    @Test
    public void test_merge() {

        ConcurrentHashMap<String, List<PeptideMatchData>> cometPsms = getCometPsms();
        ConcurrentHashMap<String, List<PeptideMatchData>> mqPsms = getMQPsms();
        ConcurrentHashMap<String, List<PeptideMatchData>> combined = new ConcurrentHashMap<>();

        CometMaxQuantPsmCombiner combiner = new CometMaxQuantPsmCombiner(mqPsms,combined);
        cometPsms.forEach(combiner);

        Assert.assertTrue(combined.size()==1);
        Assert.assertTrue(combined.get("spec1").size()==2);
        Assert.assertEquals("PEPTIDE",combined.get("spec1").get(0).getPeptide().toString());
        Assert.assertEquals("PEPTIDER",combined.get("spec1").get(1).getPeptide().toString());

    }

    public static ConcurrentHashMap<String, List<PeptideMatchData>> getCometPsms()
    {

        ConcurrentHashMap<String, List<PeptideMatchData>> psms = new ConcurrentHashMap<>();

        Set<String> prots = new HashSet<>();
        prots.add("protein1");
        prots.add("protein2");

        NewAnceParams params = NewAnceParams.getInstance();


        TObjectDoubleMap<String> scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("xcorr",1.0);
        scoreMap.put("deltacn",0.1);
        scoreMap.put("spscore",100.0);

        PeptideMatchData peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPTIDE"), prots, scoreMap, 1, false);

        scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("xcorr",2.0);
        scoreMap.put("deltacn",0.2);
        scoreMap.put("spscore",200.0);

        PeptideMatchData peptideMatchData2 = new PeptideMatchData(Peptide.parse("PEPTIDER"), prots, scoreMap, 1, false);

        psms.put("spec1",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec1").add(peptideMatchData1);
        psms.get("spec1").add(peptideMatchData2);

        scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("xcorr",3.0);
        scoreMap.put("deltacn",0.3);
        scoreMap.put("spscore",300.0);

        peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPTIDE"), prots, scoreMap, 1, false);
        psms.put("spec2",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec2").add(peptideMatchData1);

        peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPTIDE"), prots, scoreMap, 1, false);
        psms.put("spec5",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec5").add(peptideMatchData1);

        return psms;
    }

    public static ConcurrentHashMap<String, List<PeptideMatchData>> getMQPsms()
    {

        ConcurrentHashMap<String, List<PeptideMatchData>> psms = new ConcurrentHashMap<>();

        Set<String> prots = new HashSet<>();
        prots.add("protein1");
        prots.add("protein2");

        NewAnceParams params = NewAnceParams.getInstance();


        TObjectDoubleMap<String> scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("score",1.0);

        PeptideMatchData peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPTIDE"), prots, scoreMap, 1, false);

        scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("score",2.0);

        PeptideMatchData peptideMatchData2 = new PeptideMatchData(Peptide.parse("PEPTIDER"), prots, scoreMap, 1, false);

        psms.put("spec1",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec1").add(peptideMatchData1);
        psms.get("spec1").add(peptideMatchData2);

        scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("score",3.0);

        peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPDIDE"), prots, scoreMap, 1, false);
        psms.put("spec2",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec2").add(peptideMatchData1);

        scoreMap = new TObjectDoubleHashMap<>();
        scoreMap.put("score",4.0);

        peptideMatchData1 = new PeptideMatchData(Peptide.parse("PEPTIDE"), prots, scoreMap, 1, false);
        psms.put("spec3",Collections.synchronizedList(new ArrayList<>()));
        psms.get("spec3").add(peptideMatchData1);

        return psms;
    }
}
