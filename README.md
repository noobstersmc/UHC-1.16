# UHC-1.16
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=noobstersmc_UHC-1.16&metric=bugs&token=95001eeea391c4d25c5b013f7a19a31f1689dcde)](https://sonarcloud.io/dashboard?id=noobstersmc_UHC-1.16)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=noobstersmc_UHC-1.16&metric=ncloc&token=95001eeea391c4d25c5b013f7a19a31f1689dcde)](https://sonarcloud.io/dashboard?id=noobstersmc_UHC-1.16)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=noobstersmc_UHC-1.16&metric=sqale_index&token=95001eeea391c4d25c5b013f7a19a31f1689dcde)](https://sonarcloud.io/dashboard?id=noobstersmc_UHC-1.16)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=noobstersmc_UHC-1.16&metric=duplicated_lines_density&token=95001eeea391c4d25c5b013f7a19a31f1689dcde)](https://sonarcloud.io/dashboard?id=noobstersmc_UHC-1.16)


## How to compile

Clone https://github.com/noobstersmc/Kern and run ./gradlew publishToMavenLocal to get the kern dependency

Run `./gradlew clean build` on this repo and you should get a `-all` binary on build/libs directory

## How to run

Download the condor eggs for [UHC](https://github.com/noobstersmc/Condor-Eggs/tree/UHC) or [UHC_RUN](https://github.com/noobstersmc/Condor-Eggs/tree/UHC-Run) from github. The image folder contains the original contents of the image used in production back in the original server, you'll need to provide a paperspigot-1.16 jar and edit some configs.

Changes needed:
1. Enabled online-mode on server.properties
2. On paper.yml disable velocity mode
3. On condor/databases.json replace the connection string with a valid mongo connection string.
4. Add your newly compiled jar and Kern from this repo to that.

You will also need a redis server or you can comment out CondorManager before compiling the jar on https://github.com/noobstersmc/UHC-1.16/blob/redesign/src/main/java/me/noobsters/minigame/UHC.java#L195

