$enc = New-Object System.Text.UTF8Encoding $false
$layoutDir = "D:\2nd year\Mobile\APP\Women_Safety_App\app\src\main\res\layout"
$results = ""

Get-ChildItem -Path $layoutDir -Filter "*.xml" | ForEach-Object {
    $bytes = [System.IO.File]::ReadAllBytes($_.FullName)
    $hasBom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
    if ($hasBom) {
        $content = [System.IO.File]::ReadAllText($_.FullName)
        [System.IO.File]::WriteAllText($_.FullName, $content, $enc)
        $results += "FIXED BOM: $($_.Name)`r`n"
    } else {
        $results += "OK: $($_.Name)`r`n"
    }
}

$locFile = Join-Path $layoutDir "activity_location.xml"
$text = [System.IO.File]::ReadAllText($locFile)
if ($text -match 'android:name=') {
    $text = $text.Replace('android:name="com.google.android.gms.maps.SupportMapFragment"', 'class="com.google.android.gms.maps.SupportMapFragment"')
    [System.IO.File]::WriteAllText($locFile, $text, $enc)
    $results += "FIXED android:name in activity_location.xml`r`n"
}

Get-ChildItem -Path $layoutDir -Filter "*_new.xml" -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue

[System.IO.File]::WriteAllText("D:\2nd year\Mobile\APP\Women_Safety_App\fix_results.txt", $results)

