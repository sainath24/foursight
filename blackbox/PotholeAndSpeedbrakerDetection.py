#!/usr/bin/env python
# coding: utf-8

# In[1]:


# %tensorflow_version 2.x

# Import TensorFlow and tf.keras
import tensorflow as tf
keras = tf.keras


# In[2]:


import pandas as pd
import numpy as np


# In[3]:


# Let`s import all packages that we may need:

import sys 
import numpy as np # linear algebra
from scipy.stats import randint
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv), data manipulation as in SQL
import matplotlib.pyplot as plt # this is used for the plot the graph 
import seaborn as sns # used for plot interactive graph. 
from sklearn.model_selection import train_test_split # to split the data into two parts
#from sklearn.cross_validation import KFold # use for cross validation
from sklearn.preprocessing import StandardScaler # for normalization
from sklearn.preprocessing import MinMaxScaler
from sklearn.pipeline import Pipeline # pipeline making
from sklearn.model_selection import cross_val_score
from sklearn.feature_selection import SelectFromModel
from sklearn import metrics # for the check the error and accuracy of the model
from sklearn.metrics import mean_squared_error,r2_score

## for Deep-learing:
import keras
from keras.layers import Dense
from keras.models import Sequential
from keras.utils import to_categorical
from keras.optimizers import SGD 
from keras.callbacks import EarlyStopping
from keras.utils import np_utils
import itertools
from keras.layers import LSTM
from keras.layers.convolutional import Conv1D
from keras.layers.convolutional import MaxPooling1D
from keras.layers import Dropout


# In[4]:


DATA_DIR="paniitdata/"


# # Labels - 0: normal, 1:pothole, 2:speed breaker

# In[5]:


testing = pd.read_csv(DATA_DIR+"acc_dataset2.csv")
testing[testing.isnull().any(axis=1)]


# In[6]:


testing.drop(40601,inplace=True)


# In[7]:


test_vals = testing.values


# In[8]:


testing.head()


# In[9]:


def series_to_supervised(data, n_in=1, n_out=1, dropnan=True):
    n_vars = 1 if type(data) is list else data.shape[1]
    dff = pd.DataFrame(data)
    cols, names = list(), list()
    # input sequence (t-n, ... t-1)
    for i in range(n_in, 0, -1):
        cols.append(dff.shift(i))
        names += [('var%d(t-%d)' % (j+1, i)) for j in range(n_vars)]
    # forecast sequence (t, t+1, ... t+n)
    for i in range(0, n_out):
        cols.append(dff.shift(-i))
        if i == 0:
            names += [('var%d(t)' % (j+1)) for j in range(n_vars)]
        else:
            names += [('var%d(t+%d)' % (j+1, i)) for j in range(n_vars)]
    # put it all together
    agg = pd.concat(cols, axis=1)
    agg.columns = names
    # drop rows with NaN values
    if dropnan:
        agg.dropna(inplace=True)
    return agg


# In[10]:


reframed_test = series_to_supervised(test_vals, 1, 1)
#reframed.head()
# drop columns we don't want to predict
reframed_test.drop(reframed_test.columns[[4,5,6]], axis=1, inplace=True)
reframed_test.head()


# In[11]:


new_vals = reframed_test.values


# In[12]:


new_test_y = new_vals[:, -1]
new_test_y.shape


# In[13]:


new_test_x = new_vals[:, :-1]


# In[14]:


new_test_x = new_test_x.reshape((new_test_x.shape[0], 1, new_test_x.shape[1]))


# In[15]:


new_test_x.shape


# In[16]:


from keras.models import model_from_json
# load json and create model
json_file = open('model.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)
# load weights into new model
model.load_weights("mymodel.h5")
print("Loaded model from disk")


# In[17]:


model.compile(loss='mean_squared_error', optimizer='adam')


# In[18]:


yhat_new = model.predict(new_test_x)


# In[24]:


aa=[x for x in range(40600)]
plt.figure(figsize=(15,20))
plt.plot(aa, new_test_y[0:40600], marker='.', label="actual")
plt.plot(aa, yhat_new[0:40600], 'r', label="prediction")
plt.ylabel('Action', size=15)
plt.xlabel('Time step', size=15)
plt.legend(fontsize=15)
plt.savefig("predictions_on_test_set.png")
#plt.show()
print("Plot generated!");


# In[ ]:




