CRAR - CRowd Answer Recommender
=========================================================================================

This repository contains the source code to compare all baselines (using HIT,MRR,MAP,MR) in the paper below.


```
Improved Retrieval of Programming Solutions with Code Examples Using a Multi-featured Score
Rodrigo F. Silva; Mohammad Masudur Rahman; Carlos Eduardo C. Dantas; Chanchal Roy; Foutse Khomh; Marcelo A. Maia
```

Prerequisites
-----------------------------------------------------------

1) Hardware:

```
86GB RAM or 16GB RAM + 60GB Linux SWAP (To use SWAP, a nvme SSD is highly recommended).
```

2) Software:

```
1. Java 1.8
2. Spring Tool Suite 4 (https://spring.io/tools) or Eclipse IDE with Spring Tools plugin
3. Postgres 12.7 - Configure your DB to accept local connections. An example of pg_hba.conf configuration:
```

```
..
\# TYPE  DATABASE        USER            ADDRESS                 METHOD
\# "local" is for Unix domain socket connections only
local   all             all                                     md5
\# IPv4 local connections:
host    all             all             127.0.0.1/32            md5
..
```

4. PgAdmin (we used PgAdmin 4) but feel free to use any DB tool for PostgreSQL.


Configuring the CRAR database
-----------------------------------------------------------

1. Download the SO Dump of March 2019 (backup2019crar-min.backup - available on Zenodo https://zenodo.org/record/5115300#.YPyOcDrQ9H7). This is a preprocessed dump, downloaded from the official web site containing the main tables we use. We only consider Java posts. Postsmin table (representing posts table) has extra columns with the preprocessed data used by Crar.

2. On your DB tool, create a new database named stackoverflow2019journaldycrokage. This is a query example:

```
CREATE DATABASE stackoverflow2019journaldycrokage
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
```

3. Restore the downloaded dump to the created database.
    
Obs: restoring this dump would require at least 30 Gb of free space. If your operating system runs in a partition with insufficient free space, create a tablespace pointing to a larger partition and associate the database to it by replacing the "TABLESPACE" value to the new tablespace name: TABLESPACE = tablespacename.

4. Assert the database is sound. Execute the following SQL command: 
    
```
select id, title,sentencevectors from postsmin po limit 100
```

Running CRAR
-----------------------------------------------------------

1) You can import the project in Spring Tool Suite: 

```
right mouse button - import - Projects from git - Clone URI

https://github.com/ISEL-UFU/crar-baselines

next - next - Import using the new Project Wizard - Maven - Check out Maven Projects from SCM - https://github.com/ISEL-UFU/crar-baselines
```

Or you can clone this project with the command 

```
git clone https://github.com/ISEL-UFU/crar-baselines
```
And import the maven project in Spring Tool Suite.

2) You can alter the database configurations on src/main/resources/config/application.properties

```
CRAR_HOME = the root folder of the project (ex /home/user/crar) where you must place the project artifacts (crar.jar, data folder, replication-package-application.properties).

spring.datasource.username = your db user

spring.datasource.password= = your db password

spring.datasource.url= your database URL, as for example: jdbc:postgresql://localhost:5432/stackoverflow2019journaldycrokage.
```

3) The java file to run the game is Initializer.java

On Run configurations, you need to set VM arguments: 

```
-Xms2048m -Xmx45000m
```


Aditional Information
-----------------------------------------------------------

1) You can see the baselines on CrarApp.java class

```
//RQ1 - Table 4 - SF = Social Feature. TAS = Total Answer Score. AC = Answers Count. QS = Question Score.
baselines.add(new Baseline("Template-Without-SF", language, false, "None", "", false,	CrarParameters.roundsWithoutSocialFactors));
baselines.add(new Baseline("Template-SF-TAS", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-SF-TAS-AC", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-SF-QS", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-SF-AC", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-SF-QS-TAS", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template (SF-ALL)", language, false, "None", "", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-SF-QS-AC", language, false, "None", "", false,CrarParameters.roundsWithSocialFactors));			
												
//RQ1 - Table 5 - Ant = Antonimo. NN = Nouns. VB = Verbs. TR = Threads. ANS = Answers
baselines.add(new Baseline("Template-Ant-NN_VB-TR_ANS", language, false, "All", "threads&answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-VB-ANS", language, false, "VB", "answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-VB-TR_ANS", language, false, "VB", "threads&answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN_VB-TR", language, false, "All", "threads", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN-TR_ANS", language, false, "NN", "threads&answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN_VB-ANS", language, false, "All", "answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template", language, false, "None", "", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-VB-TR", language, false, "VB", "threads", false, CrarParameters.roundsWithSocialFactors));			
baselines.add(new Baseline("Template-Ant-NN-TR", language, false, "NN", "threads", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN-ANS", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
	
//RQ1 - Table 6 - CRAR
baselines.add(new Baseline("Template (SF-ALL)", language, false, "None", "", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN-ANS|SF-QS-AC", language, false, "NN", "answers", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("Template-Ant-NN-ANS|SF-All (CRAR)", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
	
//RQ2 - Table 7
baselines.add(new Baseline("TF", language, false, "NN", "answers", false, CrarParameters.roundsWithoutSocialFactors)); 
baselines.add(new Baseline("Sent2Vec", language, false, "NN", "answers", false, CrarParameters.roundsWithoutSocialFactors)); 
baselines.add(new Baseline("CNN", language, false, "NN", "answers", false, CrarParameters.roundsWithoutSocialFactors)); 
baselines.add(new Baseline("Asym.Sim.Body+Ans.Body", language, false, "NN", "answers", false, CrarParameters.roundsWithoutSocialFactors)); 
baselines.add(new Baseline("Asym.Sim.Title", language, false, "NN", "answers", false, CrarParameters.roundsWithoutSocialFactors)); 
baselines.add(new Baseline("CRAR Without TF", language, false, "NN", "answers", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("CRAR Without Sent2Vec/CNN", language, false, "NN", "answers", false,CrarParameters.roundsWithSocialFactors));			
baselines.add(new Baseline("CRAR Without Asym.Sim.Body+Ans.Body", language, false, "NN", "answers", false,CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("CRAR Without Asym.Sim.Title", language, false, "NN", "answers", false,CrarParameters.roundsWithSocialFactors));		    						  			
baselines.add(new Baseline("CRAR-CNN", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
			
//RQ3 - Table 8
baselines.add(new Baseline("Top Method", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors)); 
baselines.add(new Baseline("TF-IDF", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors)); 
baselines.add(new Baseline("Thread Score", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors)); 	
baselines.add(new Baseline("Asymmetric Similarity", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors)); 
baselines.add(new Baseline("CRAR Without TF-IDF", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("CRAR Without Asymmetric Similarity", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));     	 		     
baselines.add(new Baseline("CRAR Without Thread Score", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
baselines.add(new Baseline("CRAR Without Top Method", language, false, "NN", "answers", false, CrarParameters.roundsWithSocialFactors));
	        
//RQ4 - Table 9 - CROKAGE - Como no baseline do EMSE-REPLICATION-PACKAGE com API class factor
baselines.add(new Baseline("CROKAGE", language, true, "None", "", false, CrarParameters.roundsOnlyTRM));	        
```

You can comment some baselines to run a bit faster. CNN and CROKAGE baselines has some aditional time to run

2. The groundtruth is in file below - the baselines are compared using the 57 queries on file below.

```
data/groundTruthThreadsForQueries-java-test-57.txt
```
