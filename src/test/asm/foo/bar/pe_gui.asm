format pe gui 4.0

include 'win32a.inc'

invoke MessageBoxA,0,message,title,MB_ICONQUESTION+MB_YESNO
invoke ExitProcess,0

message db 'Can you see this message box?',0
title db 'Question',0

data import

library kernel32,'KERNEL32.DLL',user32,'USER32.DLL'

import kernel32,ExitProcess,'ExitProcess'
import user32,MessageBoxA,'MessageBoxA'

end data
