#!/bin/sh
#cd "$(dirname "$0")"
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
#Script to call Storm Model Checker and calculate the CTMC Properties
echo "CTMC Properties" > "$DIR/../output.txt"
#Identifying the number of threads located in the model
nthreads=$(grep -i 'nthreads' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=')

#Dropped Requests
var1=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"droppedRequests\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
varT=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"droppedRequests\"}=?[C<=T]" | grep "Model checking property" | sed 's/[^0-9]*//g')
answer1=$(bc <<< "scale=2;$var1/$varT")
answerF1="Dropped Requests: $answer1"
echo $answerF1 >> "$DIR/../output.txt"
#echo $answerF1

#Served Requests
var2=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"served\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer2=$(bc <<< "scale=2;$var2/$varT" | awk '{printf "%.2f", $0}') 
answerF2="Served Requests: $answer2"
echo $answerF2 >> "$DIR/../output.txt"
#echo $answerF2 

#Processing Time
var3=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"processingTime\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer3=$(bc <<< "scale=3;$var3/$var2"| awk '{printf "%.2f", $0}')
answerF3="Processing Time: $answer3"
echo $answerF3 >> "$DIR/../output.txt"
#echo $answerF3

#System Response Time
var4=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"qLen\"}=?[S]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
var4=$(bc <<< "scale=3;$var4/$nthreads+1")
answer4a=$(bc <<< "scale=3;$var4*$var3")
answer4b=$(bc <<< "scale=2;$answer4a/$var2" | awk '{printf "%.2f", $0}')
answerF4="System_RT: $answer4b"
echo $answerF4 >> "$DIR/../output.txt"
#echo $answerF4 

#Technical Analysis Response Time
var5a=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"taTime\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
var5b=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"servedTA\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer5=$(bc <<< "scale=2;$var4*$var5a/$var5b"| awk '{printf "%.2f", $0}')
answerF5="TA_RT: $answer5"
echo $answerF5 >> "$DIR/../output.txt"
#echo $answerF5

#System Success Probability
var6=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"extFails\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer6=$(bc <<< "scale=3;($var2-$var6)/($var2+$var1)"| awk '{printf "%.2f", $0}')
answerF6="Success Prob.: $answer6"
echo $answerF6 >> "$DIR/../output.txt"
echo $answerF6 >> "$DIR/../reliability.txt"
#echo $answerF6

#Technical Analysis Cost
var7=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"TAcost\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
varNReqs=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"numOfReqsHandled\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer7=$(bc <<< "scale=2;$var7/$varNReqs")
answerF7="TA Cost: $answer7"
echo $answerF7 >> "$DIR/../output.txt"
#echo $answerF7

#External Components Cost
var8=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"extCompCost\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer8=$(bc <<< "scale=2;$var8/$varNReqs")
answerF8="External Comp. Cost: $answer8"
echo $answerF8 >> "$DIR/../output.txt"
#echo $answerF8

#Overall Cost
answer9=$(bc <<< "scale=2;$answer7+$answer8")
answerF9="Overall Cost: $answer9"
echo $answerF9 >> "$DIR/../output.txt"
echo $answerF9 >> "$DIR/../cost.txt"
#echo $answerF9

#Probabilities of Executing Specified Paths

#MWTAOrNoPath
var10=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "P=?[\"MWTAOrNoPath\" U \"MWTAOrNoPath_END\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer10="MWTAOrNoPath Prob.: $var10"
echo $answer10 >> "$DIR/../output.txt"
#echo $answer10

#MWTAalarmPath
var11=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "P=?[\"MWTAalarmPath\" U \"MWTAalarmPath_END\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer11="MWTAalarmPath Prob.: $var11"
echo $answer11 >> "$DIR/../output.txt"
#echo $answer11

#FAOrNoPath
var12=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "P=?[\"FAOrNoPath\" U \"FAOrNoPath_END\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer12="FAOrNoPath Prob.: $var12"
echo $answer12 >> "$DIR/../output.txt"
#echo $answer12

#Number of Invocations (per time unit)
echo "--No. of Invocations (per time unit)" >> "$DIR/../output.txt"
#echo "--No. of Invocations (per time unit)"
#MW
var13=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"MWcount\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer13=$(bc <<< "scale=2;$var13/$varT" | awk '{printf "%.2f", $0}')
answerF13="MW: $answer13"
echo $answerF13 >> "$DIR/../output.txt"
#echo $answerF13
#FA
var14=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"FAcount\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer14=$(bc <<< "scale=2;$var14/$varT" | awk '{printf "%.2f", $0}')
answerF14="FA: $answer14"
echo $answerF14 >> "$DIR/../output.txt"
#echo $answerF14
#Order
var15=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"Ordercount\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer15=$(bc <<< "scale=2;$var15/$varT" | awk '{printf "%.2f", $0}')
answerF15="Order: $answer15"
echo $answerF15 >> "$DIR/../output.txt"
#echo $answerF15
#TA
var16=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"servedTA\"}=?[C<=T]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g')
answer16=$(bc <<< "scale=2;$var16/$varT" | awk '{printf "%.2f", $0}')
answerF16="TA: $answer16"
echo $answerF16 >> "$DIR/../output.txt"
#echo $answerF16

#Number of Invocations (per request)
echo "--No. of Invocations (per request)" >> "$DIR/../output.txt"
#echo "--No. of Invocations (per request)"
#MW
var17=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"MWcount\"}=?[F\"StatesForCount\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer17="MW: $var17"
echo $answer17 >> "$DIR/../output.txt"
#echo $answer17 
#FA
var18=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"FAcount\"}=?[F\"StatesForCount\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer18="FA: $var18"
echo $answer18 >> "$DIR/../output.txt"
#echo $answer18
#Order
var19=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"Ordercount\"}=?[F\"StatesForCount\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer19="Order: $var19"
echo $answer19 >> "$DIR/../output.txt"
#echo $answer19
#TA
var20=$(/usr/local/bin/storm -pc --prism "$DIR/../models/FX_System.sm" --prop "R{\"servedTA\"}=?[F\"StatesForCount\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states): //g' | awk '{printf "%.2f", $0}')
answer20="TA: $var20"
echo $answer20 >> "$DIR/../output.txt"
#echo $answer20

#DETECTION OF ANTIPATTERNS

typeOfAP=$(grep -i 'typeOfAP' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=')

ProcessingTime=0.6

AntipatternCheck=0

Utilization=$(grep -i 'Utilization' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=')
typeOfRun=$(grep -i 'typeOfRun' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=')

mw=0
fa=0
o=0
ta=0

MWrate=$(grep -i 'mwrate' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=' | bc)
FArate=$(grep -i 'farate' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=' | bc)
OrderRate=$(grep -i 'orderrate' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=' | bc)
taRate=$(grep -i 'ta1rate' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=' | bc)

TotalNoInvoPerReq=$(bc <<< "scale=2;$var17+$var18+$var19+$var20")
AverageNoInvoPerReq=$(bc <<< "scale=2;$TotalNoInvoPerReq/4")
#threshold=$(bc <<< "scale=2;$AverageNoInvoPerReq*0.3")
#AverageNoInvoPerReq=$(bc <<< "scale=2;$AverageNoInvoPerReq+$threshold")

TotalNoInvoPerTimeUnit=$(bc <<< "scale=2;$answer13+$answer14+$answer15+$answer16")
AverageNoInvoPerTimeUnit=$(bc <<< "scale=2;$TotalNoInvoPerTimeUnit/4")
#threshold=$(bc <<< "scale=2;$AverageNoInvoPerTimeUnit*0.3")
#AverageNoInvoPerTimeUnit=$(bc <<< "scale=2;$AverageNoInvoPerTimeUnit+$threshold")

varMW=$(bc <<< "scale=2;$answer13/$MWrate")
varFA=$(bc <<< "scale=2;$answer14/$FArate")
varO=$(bc <<< "scale=2;$answer15/$OrderRate")
varTA=$(bc <<< "scale=2;$answer16/$taRate")

NoInvDivServRate=$(bc <<< "scale=2;$varMW+$varFA+$varO+$varTA")
NoInvDivServRate=$(bc <<< "scale=2;$NoInvDivServRate/4" | awk '{printf "%.2f", $0}')

if (($typeOfRun == 1));
	then
		NoInvDivServRateMAX=$NoInvDivServRate
		NoInvDivServRateMIN=$NoInvDivServRate
		UtilizationMAX=$Utilization
		UtilizationMIN=$Utilization
elif (($typeOfRun == 2));
	then
		thres=$(bc <<< "scale=2;$NoInvDivServRate*0.1")
		NoInvDivServRateMAX=$(bc <<< "scale=2;$NoInvDivServRate+$thres")
		NoInvDivServRateMIN=$(bc <<< "scale=2;$NoInvDivServRate-$thres")
		utilThresh=$(bc <<< "scale=2;$Utilization*0.1")
		UtilizationMAX=$(bc <<< "scale=2;$Utilization+$utilThresh")
		UtilizationMIN=$(bc <<< "scale=2;$Utilization-$utilThresh")
elif (($typeOfRun == 3));
	then
		thres=$(bc <<< "scale=2;$NoInvDivServRate*0.2")
		NoInvDivServRateMAX=$(bc <<< "scale=2;$NoInvDivServRate+$thres")
		NoInvDivServRateMIN=$(bc <<< "scale=2;$NoInvDivServRate-$thres")
		utilThresh=$(bc <<< "scale=2;$Utilization*0.2")
		UtilizationMAX=$(bc <<< "scale=2;$Utilization+$utilThresh")
		UtilizationMIN=$(bc <<< "scale=2;$Utilization-$utilThresh")
elif (($typeOfRun == 4));
	then
		thres=$(bc <<< "scale=2;$NoInvDivServRate*0.3")
		NoInvDivServRateMAX=$(bc <<< "scale=2;$NoInvDivServRate+$thres")
		NoInvDivServRateMIN=$(bc <<< "scale=2;$NoInvDivServRate-$thres")
		utilThresh=$(bc <<< "scale=2;$Utilization*0.3")
		UtilizationMAX=$(bc <<< "scale=2;$Utilization+$utilThresh")
		UtilizationMIN=$(bc <<< "scale=2;$Utilization-$utilThresh")
elif (($typeOfRun == 5));
	then
		thres=$(bc <<< "scale=2;$NoInvDivServRate*0.45")
		NoInvDivServRateMAX=$(bc <<< "scale=2;$NoInvDivServRate+$thres")
		NoInvDivServRateMIN=$(bc <<< "scale=2;$NoInvDivServRate-$thres")
		utilThresh=$(bc <<< "scale=2;$Utilization*0.45")
		UtilizationMAX=$(bc <<< "scale=2;$Utilization+$utilThresh")
		UtilizationMIN=$(bc <<< "scale=2;$Utilization-$utilThresh")
fi

if (($typeOfAP == 1));
	then
#BLOB

if [[ $(echo "$var17 > $AverageNoInvoPerReq" | bc) -eq 1 ]]
	then
		mw=1
fi
if [[ $(echo "$var18 > $AverageNoInvoPerReq" | bc) -eq 1 ]]
	then
		fa=1
fi
if [[ $(echo "$var19 > $AverageNoInvoPerReq" | bc) -eq 1 ]]
	then
		o=1
fi
if [[ $(echo "$varTA > $UtilizationMAX" | bc) -eq 1 && $(echo "$var20 > $AverageNoInvoPerReq" | bc) -eq 1 && $(echo "$varTA > $NoInvDivServRateMAX" | bc) -eq 1 ]]
	then
		ta=1
fi

totalcount=$(bc <<< "scale=2;$mw+$fa+$o+$ta")
if (($totalcount > 0));
	then
		if (($mw == 1));
			then
				echo "BLOB(MW)"
				#echo "BLOB(MW)" >> "$DIR/../antipatterns.txt"
				AntipatternCheck=$((AntipatternCheck + 1))
		fi
		if (($fa == 1));
			then
				echo "BLOB(FA)"
				#echo "BLOB(FA)" >> "$DIR/../antipatterns.txt"
				AntipatternCheck=$((AntipatternCheck + 1))
		fi
		if (($o == 1));
			then
				echo "BLOB(Order)"
				#echo "BLOB(Order)" >> "$DIR/../antipatterns.txt"
				AntipatternCheck=$((AntipatternCheck + 1))
		fi
		if (($ta == 1));
			then
				echo "BLOB(TA)"
				#echo "BLOB(TA)" >> "$DIR/../antipatterns.txt"
				AntipatternCheck=$((AntipatternCheck + 1))
		fi
fi

elif (($typeOfAP == 2));
	then
#CPS

DroppedRequests=10
Served=3
SystemRT=3

operationMW=$(grep -i 'operation' "$DIR/../config/marketWatch.cfg" | grep '=' | cut -f2 -d'=')
operationFA=$(grep -i 'operation' "$DIR/../config/fundamentalAnalysis.cfg" | grep '=' | cut -f2 -d'=')
operationOrder=$(grep -i 'operation' "$DIR/../config/order.cfg" | grep '=' | cut -f2 -d'=')

responseTimeMW=$(grep -i 'MWrate' "$DIR/../config/FX_System.cfg" | grep '/' | cut -f2 -d'/')
responseTimeFA=$(grep -i 'FArate' "$DIR/../config/FX_System.cfg" | grep '/' | cut -f2 -d'/')
responseTimeOrder=$(grep -i 'OrderRate' "$DIR/../config/FX_System.cfg" | grep '/' | cut -f2 -d'/')

averageResponseTime=$(bc <<< "scale=2;$responseTimeMW+$responseTimeFA+$responseTimeOrder")
averageResponseTime=$(bc <<< "scale=2;$averageResponseTime/3" | awk '{printf "%.2f", $0}')

if [[ $(echo "$responseTimeFA > $averageResponseTime" | bc) -eq 1 && ("$operationFA" == *"PAR"* || "$operationFA" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(FA)max"
elif [[ $(echo "$responseTimeFA < $averageResponseTime" | bc) -eq 1 && ("$operationFA" == *"PAR"* || "$operationFA" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(FA)min"
fi

if [[ $(echo "$responseTimeMW > $averageResponseTime" | bc) -eq 1 && ("$operationMW" == *"PAR"* || "$operationMW" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(MW)max"
elif [[ $(echo "$responseTimeMW < $averageResponseTime" | bc) -eq 1 && ("$operationMW" == *"PAR"* || "$operationMW" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(MW)min"
fi

if [[ $(echo "$responseTimeOrder > $averageResponseTime" | bc) -eq 1 && ("$operationOrder" == *"PAR"* || "$operationOrder" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(Order)max"
elif [[ $(echo "$responseTimeOrder < $averageResponseTime" | bc) -eq 1 && ("$operationOrder" == *"PAR"* || "$operationOrder" == *"par"*) ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(Order)min"
fi

if [[ $(echo "$varTA > $UtilizationMAX" | bc) -eq 1 && $(echo "$varTA > $NoInvDivServRateMAX" | bc) -eq 1 ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(TA)max"
elif [[ $(echo "$varTA < $UtilizationMIN" | bc) -eq 1 && $(echo "$varTA < $NoInvDivServRateMIN" | bc) -eq 1 ]]
	then
		AntipatternCheck=$((AntipatternCheck + 1))
		echo "CPS(TA)min"
fi

elif (($typeOfAP == 3));
	then
#P&F

probPath=$(grep -i 'probPath' "$DIR/../config/FX_System.cfg"  | cut -f2 -d'=')

TotalPaths=$(bc <<< "scale=2;$var10+$var11+$var12" | awk '{printf "%.2f", $0}')
AveragePath=$(bc <<< "scale=2;$TotalPaths/3" | awk '{printf "%.2f", $0}')

MWpath=$(bc <<< "scale=2;$var10+$var11" | awk '{printf "%.2f", $0}')
FApath=$(bc <<< "scale=2;$var12" | awk '{printf "%.2f", $0}')
Orderpath=$(bc <<< "scale=2;$var10+$var12" | awk '{printf "%.2f", $0}')
TApath=$(bc <<< "scale=2;$var10+$var11" | awk '{printf "%.2f", $0}')

if [[ $(echo "$var10 > $AveragePath" | bc) -eq 1 && $(echo "$var10 > $probPath" | bc) -eq 1 && $(echo "$answer13 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(MW/MWTAOrNo)"
		AntipatternCheck=$((AntipatternCheck + 1))
elif [[ $(echo "$var11 > $AveragePath" | bc) -eq 1 && $(echo "$var11 > $probPath" | bc) -eq 1 && $(echo "$answer13 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(MW/MWTAalarm)"
		AntipatternCheck=$((AntipatternCheck + 1))
fi

if [[ $(echo "$FApath > $AveragePath" | bc) -eq 1 && $(echo "$FApath > $probPath" | bc) -eq 1 && $(echo "$answer14 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(FA/FAOrNo)"
		AntipatternCheck=$((AntipatternCheck + 1))
fi

if [[ $(echo "$var10 > $AveragePath" | bc) -eq 1 && $(echo "$var10 > $probPath" | bc) -eq 1 && $(echo "$answer15 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(Order/MWTAOrNo)"
		AntipatternCheck=$((AntipatternCheck + 1))
elif [[ $(echo "$var12 > $AveragePath" | bc) -eq 1 && $(echo "$var12 > $probPath" | bc) -eq 1 && $(echo "$answer15 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(Order/FAOrNo)"
		AntipatternCheck=$((AntipatternCheck + 1))
fi

if [[ $(echo "$var10 > $AveragePath" | bc) -eq 1 && $(echo "$var10 > $probPath" | bc) -eq 1 && $(echo "$answer16 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(TA/MWTAOrNo)"
		AntipatternCheck=$((AntipatternCheck + 1))
elif [[ $(echo "$var11 > $AveragePath" | bc) -eq 1 && $(echo "$var11 > $probPath" | bc) -eq 1 && $(echo "$answer16 > $AverageNoInvoPerTimeUnit" | bc) -eq 1 ]]
	then
		echo "P&F(TA/MWTAalarm)"
		AntipatternCheck=$((AntipatternCheck + 1))
fi

fi

if (($AntipatternCheck == 0));
	then
		echo "No Antipatterns Detected!"
fi

exit