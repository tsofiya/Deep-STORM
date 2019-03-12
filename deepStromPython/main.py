import argparse
import numpy as np

from createdata import CreateData
from Training import train_model

if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument("cameraPixelSize", type=int, help="Camera pixel size in [nm]")
    parser.add_argument("Directory", type=str, help="Files directoy")
    parser.add_argument("tifName", type=str, help="Images name")
    parser.add_argument("csvFileName", type=str, help="csv name")
    parser.add_argument("--upSamplingFactor", type=int, help="Ratio for upsampling", default=8)
    parser.add_argument("--patchBeforeUpSample", type=int, help="Patch size", default=26)

    args = parser.parse_args()


    patches, heatmaps = CreateData(args.cameraPixelSize,args.Directory,args.tifName,args.csvFileName,args.upSamplingFactor,args.patchBeforeUpSample)
    patches = np.array(patches)
    heatmaps = np.array(heatmaps)
    train_model(patches, heatmaps, 'weights', 'meanstd')
