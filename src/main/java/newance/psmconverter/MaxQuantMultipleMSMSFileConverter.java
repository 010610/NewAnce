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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Markus Müller
 */

public class MaxQuantMultipleMSMSFileConverter extends MultiplePsmFileConverter {


    public MaxQuantMultipleMSMSFileConverter(String psmRootDirName, Pattern regex) {

        super(psmRootDirName, regex);    }


    public void run() throws IOException{

        long start = System.currentTimeMillis();

        System.out.println("MaxQuant psm input path " + psmRootDirName);

        final List<File> psmFileList = new ArrayList<>();
        Files.walk(Paths.get(psmRootDirName))
                .filter(Files::isRegularFile)
                .filter(f -> regex.matcher(f.getFileName().toString()).find())
                .forEach(f -> psmFileList.add(f.toFile()));

        checkState(!psmFileList.isEmpty());

        int nrTasks = psmFileList.size();
        ExecutorService exe = Executors.newFixedThreadPool(nrTasks);

        CountDownLatch latch = new CountDownLatch(nrTasks);
        for (int i = 0; i < nrTasks; i++) {
            exe.submit(new MaxQuantMSMSConverter(psmFileList.get(i), psms, latch));
        }

        try {
            latch.await();

            System.out.println("Number of MaxQuant spectra converted: "+psms.size());
            System.out.println("MaxQuant msms.txt conversion ran in " + (System.currentTimeMillis() - start) / 1000d + "s");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        exe.shutdown();
    }
}
