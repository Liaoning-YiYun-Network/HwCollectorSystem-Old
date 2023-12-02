#!/bin/bash

declare -i filenumber=0
declare -i linenumber=0

list_alldir(){
    for file in $(ls -a $1)
    do
        if [ x"$file" != x"." -a x"$file" != x".." ];then
            if [ -d "$1/$file" ];then
                list_alldir "$1/$file"
	          else
			          echo "$1/$file"
			          filenumber=$filenumber+1
			          linenumber=$linenumber+$(cat "$1/$file"|wc -l)
            fi
        fi
    done
}

if [ "$1" = "" ];then
arg="."
else
arg="./"$1
fi

list_alldir $arg

echo "There are $filenumber files under directory: $arg"
echo "--total code lines are: $linenumber"