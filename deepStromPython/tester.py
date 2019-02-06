from MatlabFunctions import conv2

a = [[1, 2, 3], [4, 5, 6]]
b = [[7, 8, 9, ], [10, 11, 12]]
print(conv2(a, b, 'same'))
c= [[6, 5], [4, 3]]
print(conv2(a, c, 'same'))
