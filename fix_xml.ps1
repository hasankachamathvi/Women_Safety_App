$enc = New-Object System.Text.UTF8Encoding $false
$layoutDir = Join-Path $PSScriptRoot "app\src\main\res\layout"
$results = @()

Get-ChildItem -Path $layoutDir -Filter "*.xml" | ForEach-Object {
    $bytes = [System.IO.File]::ReadAllBytes($_.FullName)
    $hasBom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
    if ($hasBom) {
        $content = [System.IO.File]::ReadAllText($_.FullName)
        [System.IO.File]::WriteAllText($_.FullName, $content, $enc)
        $results += "FIXED BOM: $($_.Name)"
    } else {
        $results += "OK: $($_.Name)"
    }
}

# Also fix location xml to use class= instead of android:name=
$locFile = Join-Path $layoutDir "activity_location.xml"
if (Test-Path $locFile) {
    $text = [System.IO.File]::ReadAllText($locFile)
    if ($text -match 'android:name="com\.google\.android\.gms\.maps\.SupportMapFragment"') {
        $text = $text -replace 'android:name="com\.google\.android\.gms\.maps\.SupportMapFragment"', 'class="com.google.android.gms.maps.SupportMapFragment"'
        [System.IO.File]::WriteAllText($locFile, $text, $enc)
        $results += "FIXED android:name -> class in activity_location.xml"
    }
}

# Clean up temp files
Get-ChildItem -Path $layoutDir -Filter "*_new.xml" | Remove-Item -Force -ErrorAction SilentlyContinue

$results | Out-File -FilePath (Join-Path $PSScriptRoot "fix_results.txt") -Encoding ascii

