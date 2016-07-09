format pe console

include 'win32a.inc'

push  0
call  [exit]

data import

library msvcrt,'msvcrt.dll'

import msvcrt,exit,'exit'

end data
