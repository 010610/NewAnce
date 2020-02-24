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

import newance.psmconverter.PeptideSpectrumMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Markus Müller
 */

public abstract class SmoothedScoreHistogram extends ScoreHistogram {

    protected float tmpCnt;
    // if smoothedHistogram!=null, methods will be passed to smoothedHistogram its results returned. If smoothedHistogram==null
    // methods from ScoreHistogram will be called.
    protected SmoothedScoreHistogram smoothedHistogram;

    public SmoothedScoreHistogram(int[] nrBins) {

        super(nrBins);
        tmpCnt = 0;
        smoothedHistogram = null;
    }

    public SmoothedScoreHistogram(ScoreHistogram scoreHistogram) {

        super(scoreHistogram);
        tmpCnt = 0;
        smoothedHistogram = null;
    }

    public float getGamma(int bin) {

        if (smoothedHistogram!=null)
            return smoothedHistogram.getGamma(bin);
        else
            return super.getGamma(bin);
    }

    public List<Float> getGamma() {

        if (smoothedHistogram!=null)
            return smoothedHistogram.getGamma();
        else
            return super.getGamma();
    }


    public float getLocalFDR(PeptideSpectrumMatch psm) {

        int bin = index(psm);

        if (smoothedHistogram!=null)
            return smoothedHistogram.getLocalFDR(bin);
        else
            return super.getLocalFDR(bin);
    }


    public void removeSpikeNoiseHistogram(boolean adjustTotalCounts) {
        if (smoothedHistogram==null) {
            smoothedHistogram = new CometScoreHistogram(this);
        }

        smoothedHistogram.removeSpikeNoise(adjustTotalCounts);
    }


    public void smoothHistogram(boolean adjustTotalCounts) {
        if (smoothedHistogram==null) {
            smoothedHistogram = new CometScoreHistogram(this);
        }

        smoothedHistogram.smooth(adjustTotalCounts);
    }

    public void calcLocalFDR() {
        if (smoothedHistogram!=null)
            smoothedHistogram.calcLocalFDR();

        super.calcLocalFDR();
    }

    public void calcLocalFDR(float p1_p0_ratio, ScoreHistogram parent) {
        if (smoothedHistogram!=null)
            smoothedHistogram.calcLocalFDR(p1_p0_ratio,parent);

        super.calcLocalFDR(p1_p0_ratio,parent);
    }

    @Override
    public float[] getTargetDecoyCounts(float maxlFDR) {

        if (smoothedHistogram!=null) {

            if (smoothedHistogram.getlFDR().isEmpty()) smoothedHistogram.calcLocalFDR();
            return smoothedHistogram.getTargetDecoyCounts(targetCnts,decoyCnts,indexMap,maxlFDR);
        }
        else {
            return super.getTargetDecoyCounts(maxlFDR);
        }


    }

    private float[] getTargetDecoyCounts(List<Float> tCounts, List<Float> dCounts, List<Integer> idxMap, float maxlFDR) {

        float decoySum = 0;
        float targetSum = 0;
        float decoySumP = 0;
        float targetSumP = 0;
        float fdrP = 0;

        for (int i : sortedIndexes) {

            float fdr = lFDR.get(i);
            if (fdr>maxlFDR) {
                float f = (fdr-maxlFDR)/(fdr-fdrP);
                decoySum = decoySum + f*(decoySumP-decoySum);
                targetSum = targetSum + f*(targetSumP-targetSum);
                break;
            }

            int idx = idxMap.get(psmBins.get(i));
            if (idx>=0) {
                decoySum += dCounts.get(idx);
                targetSum += tCounts.get(idx);
            }

            fdrP = fdr;
            decoySumP = decoySum;
            targetSumP = targetSum;
        }

        return new float[] {decoySum, targetSum};
    }


    // remove cells that have no target counts within a neigborhood
    private void removeSpikeNoise(boolean adjustTotalCounts) {

        List<Float> newTargetCnts = new ArrayList<>();
        List<Float> newDecoyCnts = new ArrayList<>();
        List<Integer> newPsmBins = new ArrayList<>();
        currIndex = 0;

        for (Integer bin : psmBins) {

            Set<Integer> neighbours = getNeighbourIndex(bin);
            float targetCounts = 0;
            for (Integer n : neighbours) {
                int idxNN = indexMap.get(n);
                if (idxNN>=0 && idxNN<targetCnts.size()) {
                    targetCounts += targetCnts.get(idxNN);
                }
            }

            int idx = indexMap.get(bin);
            if (targetCounts>0) {
                newTargetCnts.add(targetCnts.get(idx));
                newDecoyCnts.add(decoyCnts.get(idx));
                newPsmBins.add(bin);
                indexMap.set(bin,currIndex);
                currIndex++;
            } else {
                indexMap.set(bin,-1);
            }
        }

        targetCnts = newTargetCnts;
        decoyCnts = newDecoyCnts;
        psmBins = newPsmBins;

        if (adjustTotalCounts) adjustTotalCounts();

        gamma.clear();
        lFDR.clear();
        pFDR.clear();
    }

    protected void adjustTotalCounts() {

        tmpCnt = 0;
        targetCnts.forEach(cnt -> tmpCnt += cnt);
        if (tmpCnt>0) {
            for (int i=0;i<targetCnts.size();i++) targetCnts.set(i,targetCnts.get(i)*totTargetCnt/tmpCnt);
        } else {
            totTargetCnt = 0;
        }

        tmpCnt = 0;
        decoyCnts.forEach(cnt -> tmpCnt += cnt);
        if (tmpCnt>0) {
            for (int i=0;i<decoyCnts.size();i++) decoyCnts.set(i,decoyCnts.get(i)*totDecoyCnt/tmpCnt);
        } else {
            totDecoyCnt = 0;
        }
    }

    private void smooth(boolean adjustTotalCounts) {

        List<Float> newTargetCnts = new ArrayList<>(targetCnts);
        List<Float> newDecoyCnts = new ArrayList<>(decoyCnts);
        List<Integer> newPsmBins = new ArrayList<>(psmBins);

        List<Float> tmpTargetCnts = new ArrayList<>();
        List<Float> tmpDecoyCnts = new ArrayList<>();
        List<Integer> tmpPsmBins = new ArrayList<>();
        List<Integer> nnPsmBins = new ArrayList<>();

        for (Integer bin : psmBins) {

            tmpTargetCnts.clear();
            tmpDecoyCnts.clear();
            tmpPsmBins.clear();

            int idx = indexMap.get(bin);
            float targetCounts = targetCnts.get(idx);
            float decoyCounts = decoyCnts.get(idx);
            int cnt = 1;

            Set<Integer> neighbours = getNeighbourIndex(bin);
            for (Integer n : neighbours) {
                int idxNN = indexMap.get(n);
                if (idxNN<0) {
                    tmpTargetCnts.add(0f);
                    tmpDecoyCnts.add(0f);
                    tmpPsmBins.add(n);
                    nnPsmBins.add(n);
                } else  if (idxNN<targetCnts.size()) {
                    targetCounts += targetCnts.get(idxNN);
                    decoyCounts += decoyCnts.get(idxNN);
                }
                cnt++;
            }

            targetCounts /= cnt;
            decoyCounts /= cnt;

            newTargetCnts.addAll(tmpTargetCnts);
            newDecoyCnts.addAll(tmpDecoyCnts);
            newPsmBins.addAll(tmpPsmBins);

            for (Integer n : tmpPsmBins) {
                indexMap.set(n,currIndex);
                currIndex++;
            }

            newTargetCnts.set(idx,targetCounts);
            newDecoyCnts.set(idx,decoyCounts);
        }

        // set counts for neighbours of cells with psms
        for (Integer bin : nnPsmBins) {

            Set<Integer> neighbours = getNeighbourIndex(bin);

            int idx = indexMap.get(bin);
            float targetCounts = 0;
            float decoyCounts = 0;
            int cnt = 1;

            for (Integer n : neighbours) {
                int idxNN = indexMap.get(n);
                if (idxNN>=0 && idxNN<targetCnts.size()) {
                    targetCounts += targetCnts.get(idxNN);
                    decoyCounts += decoyCnts.get(idxNN);
                }
                cnt++;
            }

            targetCounts /= cnt;
            decoyCounts /= cnt;

            newTargetCnts.set(idx,targetCounts);
            newDecoyCnts.set(idx,decoyCounts);
        }

        targetCnts = newTargetCnts;
        decoyCnts = newDecoyCnts;
        psmBins = newPsmBins;

        if (adjustTotalCounts) adjustTotalCounts();

        gamma.clear();
        lFDR.clear();
        pFDR.clear();
    }

    public boolean isSmoothed() {
        return smoothedHistogram!=null;
    }

}
