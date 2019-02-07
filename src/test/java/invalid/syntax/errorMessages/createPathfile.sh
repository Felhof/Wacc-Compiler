for d in */ ; do
 cd $d
 rm ${d%/*}.txt
 touch ${d%/*}.txt
 for i in *.txt ; do
  if [ $i != ${d%/*}.txt ]
  then
   echo src/test/java/invalid/syntax/errorMessages/$d$i >> ${d%/*}.txt
  fi
 done
 cd ..
done 

