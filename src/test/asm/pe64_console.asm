format pe64 console
entry main

include 'win64a.inc'

section '.idata' import data readable

library msvcrt,'msvcrt.dll'

import msvcrt,\
       exit,'exit',\
       printf,'printf'

section '.data' data readable writeable

   msg db "Hello, world!",10,13,0

section '.code' code readable executable

main:
   mov   rcx, msg
   call  [printf]
   mov   rcx, 0
   call  [exit]
