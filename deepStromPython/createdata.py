import numpy as np
import csv
import random
import matplotlib.pyplot as plt

from dataimport import readStackFromTiff
from MatlabFunctions import matlab_style_gauss2D
from MatlabFunctions import conv2
from MatlabFunctions import ind2sub



def CreateData(cameraPixelSize, Dir, tifName, csvFileName, upSamplingFactor=8, patchBeforeUpSample=26):
    plt.ion()
    # number of patches to extract from each image
    numPatches = 500
    # training patch-size: needs to be dividable by 8 with no residual
    patchSize = upSamplingFactor * patchBeforeUpSample
    # maximal number of training examples
    maxExamples = 10000
    # minimal number of emitters in each patch to avoid empty examples in case  of low-density conditions
    minEmitters = 7

    matList = readStackFromTiff(Dir + "/" + tifName)
    numImages = len(matList)

    orgHeight, orgWidth = matList[0].shape
    # dimensions of the high-res grid
    highResHeight = upSamplingFactor * orgHeight
    highResWidth = upSamplingFactor * orgWidth
    ratio = cameraPixelSize / upSamplingFactor

    # heatmap psf
    psfHeatmap = matlab_style_gauss2D([7, 7], 1)

    # Number of training exemples.
    trainingExemplesNum = min(numImages * numPatches, maxExamples)

    # initialize the training patches and labels
    patches = []
    heatmaps = []
    spikes = []

    csvFile = open(Dir + "/" + csvFileName)
    csvData = list(csv.reader(csvFile))
    csvCol = csvData.pop(0)
    csvFile.close()
    if (len(csvData) < 8):
        raise NameError('csv file should hava all 8 columns')
    csvData = [[float(y) for y in x] for x in csvData]

    exampleCounter = 0
    f, (lowRes, Heatmap) = plt.subplots(1, 2, sharey=True)

    for idx, image in enumerate(matList):
        # upsample the frame by the upsampling_factor using a nearest neighbor
        resize = np.ones([upSamplingFactor, upSamplingFactor])
        resize = np.kron(image, resize)
        frameData = []
        xLocations = []
        yLocations = []
        while (len(csvData) != 0 and csvData[0][1] == idx + 1):
            row = csvData.pop(0)
            frameData.append(row)

            # get the approximated locations according to the high-res grid pixel size
            xLocations.append(max(min(round(row[2] / ratio), highResHeight - 1), 0))
            yLocations.append(max((min(round(row[3] / ratio), highResWidth - 1), 0)))
            spikeImage = np.zeros((highResHeight, highResWidth))
        for i in range(len(xLocations)):
            spikeImage[yLocations[i], xLocations[i]] = 1

        # get the labels per frame in spikes and heatmaps
        heatMapImage = conv2(spikeImage, psfHeatmap, 'same')

        # choose randomly patch centers to take as training examples
        linearIndices = range((highResHeight - patchSize) * (highResWidth - patchSize))  # need to add patchSize/2,
        # to each indices to make sure none of the patches is out of boundry.
        linearIndices = random.sample(linearIndices, 500)

        skipCounter = 0
        for lIndex in linearIndices:
            index = ind2sub(highResHeight - patchSize, lIndex)
            numEmmiters = np.count_nonzero(
                spikeImage[index[0]:(index[0] + patchSize + 1), index[1]:(index[1] + patchSize + 1)])
            if (numEmmiters < minEmitters):
                skipCounter += 1
            else:
                patches.append(resize[index[0]:(index[0] + patchSize + 1), index[1]:(index[1] + patchSize + 1)])
                spikes.append(spikeImage[index[0]:(index[0] + patchSize + 1), index[1]:(index[1] + patchSize + 1)])
                heatmaps.append(heatMapImage[index[0]:(index[0] + patchSize + 1), index[1]:(index[1] + patchSize + 1)])
                exampleCounter += 1

        lowRes.imshow(resize, cmap='gray')
        scat = lowRes.scatter(xLocations, yLocations, c='r', marker='+')
        Heatmap.imshow(heatMapImage, cmap='gray')

        plt.pause(0.1)
        scat.remove()
        if (exampleCounter > maxExamples):
            break

    return patches, heatmaps

