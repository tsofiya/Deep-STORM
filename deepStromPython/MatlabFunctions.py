import numpy as np


def matlab_style_gauss2D(shape=(3, 3), sigma=0.5):
    """
    2D gaussian mask - should give the same result as MATLAB's
    fspecial('gaussian',[shape],[sigma])
    """
    m, n = [(ss - 1.) / 2. for ss in shape]
    y, x = np.ogrid[-m:m + 1, -n:n + 1]
    h = np.exp(-(x * x + y * y) / (2. * sigma * sigma))
    h[h < np.finfo(h.dtype).eps * h.max()] = 0
    sumh = h.sum()
    if sumh != 0:
        h /= sumh
    psfHeatmap = np.mat(h)
    return psfHeatmap


if __name__ == '__main__':
    psfHeatmap= matlab_style_gauss2D([7,7], 1)
    for i in range(0, psfHeatmap.shape[0]):
        for j in range(0, psfHeatmap.shape[1]):
            print(round(psfHeatmap[i,j], 4), end='')
            print(" ", end='')
        print("\n")


