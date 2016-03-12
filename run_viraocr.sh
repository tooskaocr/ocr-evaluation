#!/bin/bash
#path=/opt/ocr-evaluation/benchmark_data/simple/text1-bnazanin-12.png

basepath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
runname=$1

if [ -z $runname ]; then
	echo "usage: run_viraocr [runname]"
	exit
fi

mkdir -p $basepath/runs/$runname


for dataset in simple medium difficult ; do
	for absfilepath in $basepath/benchmark_data/$dataset/*.{jpg,png,bmp,tiff} ; do
		if [ -e $absfilepath ]; then
			filename=$(basename "$absfilepath")
			basefilename="${filename%.*}"
			if [ -e $basepath/benchmark_data/$dataset/${basefilename}.txt ]; then
				echo Running on $absfilepath
				matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; setup; init; ocr_main_v3('$absfilepath', false, true, true); exit"
				cp /opt/farsi-ocr-engine-v3/temp/$basefilename/output.txt $basepath/runs/$runname/${basefilename}.txt
			fi
		fi
	done
done
