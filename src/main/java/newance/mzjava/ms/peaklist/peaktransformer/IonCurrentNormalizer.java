/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package newance.mzjava.ms.peaklist.peaktransformer;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import newance.mzjava.ms.peaklist.DelayedPeakProcessor;
import newance.mzjava.ms.peaklist.PeakAnnotation;

import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IonCurrentNormalizer<A extends PeakAnnotation> extends DelayedPeakProcessor<A, A> {

    private double sum = 0;

    @Override
    public void start(int size) {

        super.start(size);

        sum = 0;
    }

    @Override
    public void processPeak(double mz, double intensity, List<A> annotations) {

        sum += intensity;
        super.processPeak(mz, intensity, annotations);
    }

    @Override
    protected void processCached(TDoubleArrayList mzList, TDoubleArrayList intensityList, TIntObjectHashMap<List<A>> annotationMap) {

        for(int i = 0; i < mzList.size(); i++) {

            List<A> annotations;
            if(annotationMap.contains(i))
                annotations = annotationMap.get(i);
            else
                annotations = Collections.emptyList();

            sink.processPeak(mzList.get(i), intensityList.get(i) / sum, annotations);
        }
    }
}
