#including libraries
library(mlbench)
library(caret)
library(cvTools)
library(e1071)
library(hydroGOF)

# Import the complete file "outputFull.txt" non preprocessed one to create the plots 
# Excluded the data with zero reviews
# There was some noisy data points with Helpful review greater than total review. 
# Removed those data points

names(outputFull) <- c("review_helpfulness", "total_review","review_score", "review_length")
outputFull$Ratio <- c(outputFull$review_helpfulness/outputFull$total_review)
outputFull$Label <- c((as.integer(outputFull$Ratio*10)))
outputFull$Avg_helfulness <- c(as.integer(outputFull$Ratio*100))
View(outputFull)

# Corelation coefficient between review score and review length
cor_var <- cor(outputFull$review_length, outputFull$review_score)
# cor_var = -0.04282113

# Plotting the box plot for review_score and review_length to get the outliers
boxplot(Label~review_score, data=outputFull)
boxplot(outputFull$review_length)

# Plotting the scatter plot for review length and review score
plot(outputFull$review_length, outputFull$Avg_helfulness)
plot(outputFull$review_score, outputFull$Avg_helfulness)

# import the pre processed datafile "output.txt"
# the preprocessed file excludes the data with less than 6 total reviews
#new_data <- output
names(output) <- c("review_helpfulness", "total_review","review_score", "review_length")
output$Ratio <- c(output$review_helpfulness/output$total_review)
output$Label <- c(as.character(as.integer(output$Ratio*10)))
output$Avg_helfulness <- c(as.integer(output$Ratio*100))
View(output)

# Calculating the correlation coefficients with the complete data file
cor_score <- cor(output$Avg_helfulness, output$review_score)
# cor_score = 0.555149
cor_length <- cor(output$Avg_helfulness, output$review_length)
# cor_length = 0.1247732

#Creating  the training and testing data

#a) Create data frame with just review score, length and Label
dataset <- output
dataset$review_helpfulness <- NULL
dataset$total_review <- NULL
dataset$Ratio <- NULL
dataset$Avg_helfulness <- NULL

# creating Train and test data in 75% train data and 25% test data
indexes = sample(1:nrow(dataset), size=0.25*nrow(dataset))
test = dataset[indexes,]
dim(test)
train = dataset[-indexes,]
dim(train)


# Creating the model using Naive Bayes
mod <- naiveBayes(as.factor(Label) ~ review_score + review_length, data = train)
pr <- predict(mod , test, type = "class")
sim_val <- strtoi(pr)
obs_val <- test$Label
myrmse <- rmse(sim_val, as.numeric(unlist(obs_val)))
# output rmse 3.40199
# % error 
per_rmse = myrmse * 10
View(per_rmse)
# percentage error
# 34.0199%

