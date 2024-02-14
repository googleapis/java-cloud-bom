#!/bin/bash

# input for this script will be URL, artifact_id and service_name
# example: https://repo1.maven.org/maven2/com/google/cloud/google-cloud-vision google-cloud-vision vision

# output: a line in the format of
# artifact_id,service_name,version, and release_date for the artifacts

mavenCentralURL=$1
artifact_id=$2
service_name=$3

##why --header="User-Agent: ? -> Maven central denies CLI requests to browse a directory URL, so imitating a browser's behaviour by using this header.
wget -O mavenFile --referer --recursive -nd --no-parent \
  --header="User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36" \
  ${1}

outputFile="maven_versions_and_dates.txt"
# assume semantic versions, starting with number. Get lines from file that looks like
# '<a href="0.10.0-beta/" title="0.10.0-beta/">0.10.0-beta/</a>                                      2017-03-17 00:01         -     '
grep -E '<a href=\"[0-9]|[a-z].*\"\s' mavenFile | \
grep -v -E '(metadata)|(meta name)' | \
# remove  content between '/" title=' and '</a>'
sed -e 's/\/"\stitle=.*<\/a>//' | \
# remove content before version
sed -e 's/<a href=\"//' | \
# replace multiple spaces
sed -E 's/[[:space:]]{3,}/;/g' | \
# get version and date only
awk -F'[ ;]' '{print $1, $2}' | \
# insert artifact_id and service_name
awk '{$3=a}1' a="${artifact_id}" | \
awk '{$4=b}1' b="${service_name}" | \
sed 's/ \+/,/g'



#grep -E '<a href=".*">' mavenFile > mavenContents.txt
#
#awk  '/a/ {print  $2 "\t" $4}'  mavenContents.txt > finalContents.txt
#sed -i 1d  finalContents.txt
#sed -i '/maven-metadata/d' finalContents.txt
#sed -i 's/href="//g' finalContents.txt
#sed -i 's/"//g' finalContents.txt
#sed -i 's|/||g' finalContents.txt
#awk '{$3=a}1' a="${2}" finalContents.txt > newfile.txt
#awk '{$4=b}1' b="${3}" newfile.txt > final.txt
##cat final.txt >> cloud_java_client_library_release_dates_tsv.txt

#rm -f final.txt
#rm -f newfile.txt
#rm -f mavenFile
#rm -f mavenContents.txt
#rm -f finalContents.txt
