ROOT=pwd();
for i=0:100
    image = imread(sprintf('%s/benchmark_data/ganjoor/%d.jpg', ROOT, 140000+i*500));
    imagePadded = padarray(image, [0 floor((350-size(image,2))/2) 0], 255, 'post');
    imagePadded = padarray(imagePadded, [0 (350-size(imagePadded,2)) 0], 255, 'pre');
    if (i == 0)
        allimage = imagePadded;
    else
        allimage = [allimage; imagePadded];
    end
end
imwrite(allimage, sprintf('%s/single.png', ROOT));