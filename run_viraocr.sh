#!/bin/bash
#path=/opt/ocr-evaluation/benchmark_data/simple/text1-bnazanin-12.png

basepath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
runname=$1
datasets=$2

if [ -z $runname ]; then
	echo "usage: run_viraocr [runname] [dataset]"
	exit
fi

mkdir -p $basepath/runs/$runname

if [ "$datasets" == "" ]; then
	datasets="books simple medium difficult"
fi

for dataset in $datasets ; do
	echo $dataset
	mode=""
	deskew="true"
	if [ "$dataset" == "ganjoor" ]; then
		mode="single_line"
		deskew="false"
	fi
	if [ "$dataset" == "books" ] || [ "$dataset" == "simple" ]; then
		deskew="false"
	fi
	for absfilepath in $basepath/benchmark_data/$dataset/*.{jpg,png,bmp,tiff} ; do
		if [ -e $absfilepath ]; then
			filename=$(basename "$absfilepath")
			basefilename="${filename%.*}"
			if [ -e $basepath/benchmark_data/$dataset/${basefilename}.txt ]; then
				echo Running on $absfilepath
				if [ "$mode" == "single_line" ]; then
					matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; setup; init; ocr_main_v3('$absfilepath', 0, 0, $deskew, 'mode', '${mode}'); exit"
				else
					matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; setup; init; ocr_main_v3('$absfilepath', false, true, $deskew); exit"
				fi
				cp /opt/farsi-ocr-engine-v3/temp/$basefilename/output.txt $basepath/runs/$runname/${basefilename}.txt
			fi
		fi
	done
done
