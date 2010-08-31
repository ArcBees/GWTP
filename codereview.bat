echo off
IF [%1]==[] (
echo Usage: 
echo   codereview youremail@abc.com 22 24 "Patchset comment"
echo      Creates a new patch set for code review by comparing revision 22
echo      with revision 24. The patchset is sent on codereview.appspot.com
echo.
echo   codereview youremail@abc.com 24 32 "Patchset comment" 3234
echo      Creates a patch set by comparing revisions 24 and 32, adding the
echo      result to the codereview number 3234.
) ELSE (
IF [%5]==[] (
python upload.py --rev=%2:%3 -e %1 -m %4 
) ELSE (
python upload.py --rev=%2:%3 -e %1 -m %4 -i %5
) )
