#!/bin/bash
#path=/opt/ocr-evaluation/benchmark_data/simple/text1-bnazanin-12.png

basepath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
runname="$1"
model="$2"
datasets="$3"

if [ -z $runname ]; then
	echo "usage: run_viraocr [runname] [dataset]"
	exit
fi

mkdir -p $basepath/runs/$runname

if [ "$datasets" == "" ]; then
	datasets="books1 books2 simple medium difficult ganjoor"
fi

for dataset in $datasets ; do
	echo $dataset
	mode="normal"
	deskew="1"
	if [ "$dataset" == "ganjoor" ]; then
		mode="single_line"
		deskew="0"
	fi
	#if [ "$dataset" == "books" ] || [ "$dataset" == "simple" ]; then
	if [ "$dataset" == "simple" ]; then
		deskew="0"
	fi
	for absfilepath in $basepath/benchmark_data/$dataset/*.{jpg,png,bmp,tiff} ; do
		if [ -e $absfilepath ]; then
			filename=$(basename "$absfilepath")
			basefilename="${filename%.*}"
			if [ -e $basepath/benchmark_data/$dataset/${basefilename}.txt ]; then
				echo Running on $absfilepath
				if [ "$model" == "" ]; then
					if [ "$mode" == "single_line" ]; then
						matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; init; ocr_main_v3('$absfilepath', 0, 0, $deskew, 'mode', '${mode}'); exit" 
					else
						matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; init; ocr_main_v3('$absfilepath', 0, 0, $deskew); exit"
					fi
				else
					if [ "$mode" == "single_line" ]; then
						matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; init; ocr_main_v3('$absfilepath', 0, 0, $deskew, 'mode', '${mode}', 'model', '${model}'); exit"
					else
						matlab -nojvm -nodesktop -nosplash -r "cd /opt/farsi-ocr-engine-v3; init; ocr_main_v3('$absfilepath', 0, 1, $deskew, 'model', '${model}'); exit"
					fi
				fi
				cp /opt/farsi-ocr-engine-v3/temp/$basefilename/output.txt $basepath/runs/$runname/${basefilename}.txt
			fi
		fi
	done
done
