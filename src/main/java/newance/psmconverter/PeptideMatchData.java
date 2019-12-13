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
package newance.psmconverter;

import gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.expasy.mzjava.proteomics.mol.Peptide;

import java.util.Collections;
import java.util.Set;

/**
 * @author Markus Müller
 */

public class PeptideMatchData {

    private final Peptide peptide;
    private final Set<String> proteinAcc;
    private final TObjectDoubleMap<String> scoreMap;
    private final int charge;
    private final boolean isDecoy;

    public PeptideMatchData(Peptide peptide, Set<String> proteinAccs, TObjectDoubleMap<String> scoreMap, int charge) {

        this.peptide = peptide;
        this.proteinAcc = proteinAccs;
        this.scoreMap = new TUnmodifiableObjectDoubleMap<>(new TObjectDoubleHashMap<>(scoreMap));
        this.charge = charge;
        this.isDecoy = false;
    }

    public PeptideMatchData(Peptide peptide, Set<String> proteinAccs,
                            TObjectDoubleMap<String> scoreMap, int charge, boolean isDecoy) {

        this.peptide = peptide;
        this.proteinAcc = proteinAccs;
        this.scoreMap = new TUnmodifiableObjectDoubleMap<>(new TObjectDoubleHashMap<>(scoreMap));
        this.charge = charge;
        this.isDecoy = isDecoy;
    }

    public Peptide getPeptide() {

        return peptide;
    }

    public Set<String> getProteinAcc() {

        return Collections.unmodifiableSet(proteinAcc);
    }

    public TObjectDoubleMap<String> getScoreMap() {

        return scoreMap;
    }

    public double getScore(String score) {

        return scoreMap.get(score);
    }

    public int getCharge() {

        return charge;
    }

    public String toSymbolString() {

        return peptide.toSymbolString();
    }

    public boolean isDecoy() {

        return isDecoy;
    }

    public void addProteinAcc(Set<String> newProteinAccs) {
        this.proteinAcc.addAll(newProteinAccs);
    }
}
