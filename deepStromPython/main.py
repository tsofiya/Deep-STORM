import argparse
from dataimport import readStackFromTiff
from MatlabFunctions import matlab_style_gauss2D
import numpy as np
import csv
from scipy.misc import imresize


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("cameraPixelSize", type=int, help="Camera pixel size in [nm]")
    parser.add_argument("Directory", type=str, help="Files directoy")
    parser.add_argument("tifName", type=str, help="Images name")
    parser.add_argument("csvFileName", type=str, help="csv name")
    parser.add_argument("--upSamplingFactor", type=int, help="Ratio for upsampling", default=8)
    parser.add_argument("--patchBeforeUpSample", type=int, help="Patch size", default=26)

    args = parser.parse_args()
    # number of patches to extract from each image
    numPatches = 500
    # maximal number of training examples
    maxExamples = 10000
    # minimal number of emitters in each patch to avoid empty examples in case  of low-density conditions
    minEmitters = 7

    matList = readStackFromTiff(args.Directory + "\\" + args.tifName)
    numImages = len(matList)

    orgHeight, orgWidth = matList[0].shape
    # dimensions of the high-res grid
    highResHeight = args.upSamplingFactor * orgHeight
    highResWidth = args.upSamplingFactor * orgWidth
    ratio= args.cameraPixelSize/args.upSamplingFactor

    # heatmap psf
    psfHeatmap = matlab_style_gauss2D([7, 7], 1)

    # Number of training exemples.
    trainingExemplesNum = min(numImages * numPatches, maxExamples)

    # initialize the training patches and labels
    patches = np.zeros((highResHeight, highResWidth, trainingExemplesNum))
    heatmaps = np.zeros((highResHeight, highResWidth, trainingExemplesNum))
    spikes = np.zeros((highResHeight, highResWidth, trainingExemplesNum))

    csvFile = open(args.Directory + "\\" + args.csvFileName)
    csvData = list(csv.reader(csvFile))
    csvCol = csvData.pop(0)
    csvFile.close()
    if (len(csvData) < 8):
        raise NameError('csv file should hava all 8 columns')
    csvData = [[float(y) for y in x] for x in csvData]

    for idx, image in enumerate(matList):
        # upsample the frame by the upsampling_factor using a nearest neighbor
        resize = np.ones([args.upSamplingFactor, args.upSamplingFactor])
        resize = np.kron(image, resize)
        frameData = []
        xLocations = []
        yLocation = []
        while (csvData[0][1] == idx + 1):
            row = csvData.pop(0)
            frameData.append(row)

            # get the approximated locations according to the high-res grid pixel size
            xLocations = max(min(round(row[3] / ratio), highResHeight-1), 0)
            yLocations = max(min(round(row[4] / ratio), highResWidth-1), 0)

    return


if __name__ == "__main__":
    main()
