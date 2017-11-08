import paho.mqtt.client as mqtt
import pandas as pd
import pickle
from sklearn.linear_model import LogisticRegression
import json
import io


class test1:
	
	model = LogisticRegression()
	count=0
	rowselect=0
	mainDf=pd.DataFrame()
	begin=False
	labels=['Running','Sitting','Walking','Laying','Standing']
	#tempDf=pd.DataFrame()

	def on_connect(self,mqttc, obj, flags, rc):
	    print("rc: " + str(rc))
	    


	def on_message(self,mqttc, obj, msg):
	    #print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))
	    #print("data ",str(msg.payload))
	    temp=pd.read_csv(io.BytesIO(msg.payload), encoding='utf8', sep=",", header=None)
	    #temp.column=[1,2,3,4,5,6]
	    self.mainDf=self.mainDf.append(temp)
	    self.count+=1

	    if self.count==25:
	    	self.count=0
	    	self.rowselect+=25
	    	#print("batch",str(msg.payload).replace('b','').replace('\'',''))
	    	if self.begin:	
	    		#print(self.mainDf.shape)
	    		tempDf=self.mainDf.iloc[self.rowselect-50:self.rowselect]
	    		#tempDf=tempDf.apply(pd.to_numeric)
	    		#print(tempDf.mean(axis=0).to_frame().T.shape)
	    		row=pd.concat([tempDf.mean(axis=0).to_frame().T,tempDf.std(axis=0).to_frame().T,tempDf.min(axis=0).to_frame().T, tempDf.max(axis=0).to_frame().T, tempDf.var(axis=0).to_frame().T],axis=1)
	    		
	    		print(self.labels[self.model.predict(row)[0]-1])
	    		#print(temp)
	    	self.begin=True
	    	
	    	
	    


	def on_publish(mqttc, obj, mid):
	    print("mid: " + str(mid))


	def on_subscribe(self,mqttc, obj, mid, granted_qos):
	    print("Subscribed: " + str(mid) + " " + str(granted_qos))


	def on_log(mqttc, obj, level, string):
	    print(string)


	# If you want to use a specific client id, use
	# mqttc = mqtt.Client("client-id")
	# but note that the client id must be unique on the broker. Leaving the client
	# id parameter empty will generate a random id for you.
	def __init__(self):
		pkl_file = open('model.pkl', 'rb')
		self.model = pickle.load(pkl_file)
		
		mqttc = mqtt.Client("1111")
		mqttc.on_message = self.on_message
		mqttc.on_connect = self.on_connect
		mqttc.on_publish = self.on_publish
		mqttc.on_subscribe = self.on_subscribe
		# Uncomment to enable debug messages
		# mqttc.on_log = on_log
		mqttc.connect("192.168.137.119", 1883, 60)
		mqttc.subscribe("test", 0)
		mqttc.loop_forever()
		

	#if __name__ == "__main__": main(on_message,on_connect,on_publish,on_subscribe)
test1()