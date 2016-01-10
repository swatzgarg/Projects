from numpy import *
from sklearn.naive_bayes import GaussianNB
from sklearn.metrics.regression import mean_squared_error
from sklearn.cross_validation import train_test_split

import numpy as np

# Loading the data created from preprocessed file
def loadData(datafile, resultfile):
    datasets = np.loadtxt(datafile)
    resultsets = np.loadtxt(resultfile)
    return train_test_split(datasets, resultsets, test_size=0.25)

# Training the data.
# dataset has input with "review score" and " review text length". 
# results has the expected output. The expected output is 1/10 of  helpfulness percentage
def trainNB(results, dataset):  
    model = GaussianNB()
    model.fit(dataset,results)
    return model
# Predicting the helpfulness 
# model is our model obtained from training Naive-Bayes
# testdata is the input for which the prediction needs to be made. 
def predictNB(testdata, model):
    return model.predict(testdata)

#Computing the RMSE
def ComputeRMSE(result, predicted) :
    return np.sqrt(mean_squared_error(result, predicted))

#Main executing program

# Get input files that are created after the data sanitizing
import sys
datafile=sys.argv[1]
resultfile=sys.argv[2]

# Calling the function loadData to load the input
data_train, data_test, result_train, result_test = loadData(datafile, resultfile)

#performing the 75-25 validation
model = trainNB(result_train, data_train)
output = predictNB(data_test, model)
rmse_final= ComputeRMSE(result_test, output)

# output rmses
print("final RMSE", rmse_final)
