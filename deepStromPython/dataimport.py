#loading Tiff images and CSV Data
import numpy as np
from skimage import io


def readStackFromTiff(tifFilename):
    # load the tiff data
    Images = io.imread(tifFilename)

    retmats = []
    for mat in Images:
        retmats.append(np.mat(mat))
    return retmats


#print (readStackFromTiff(r"C:\Users\Gideon\source\repos\Deep-STORM\demo 2 - Real Microtubules\testStack_RealMicrotubules.tif"))
