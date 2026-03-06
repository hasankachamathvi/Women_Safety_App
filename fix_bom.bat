@echo off
cd /d "D:\2nd year\Mobile\APP\Women_Safety_App"
powershell -NoProfile -Command "$enc = New-Object System.Text.UTF8Encoding $false; $dir = 'D:\2nd year\Mobile\APP\Women_Safety_App\app\src\main\res\layout'; Get-ChildItem $dir -Filter *.xml | ForEach-Object { $b=[System.IO.File]::ReadAllBytes($_.FullName); $bom=($b.Length -ge 3 -and $b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF); if($bom){ $c=[System.IO.File]::ReadAllText($_.FullName); [System.IO.File]::WriteAllText($_.FullName,$c,$enc); 'FIXED:'+$_.Name } else { 'OK:'+$_.Name } }" > fix_results.txt 2>&1

