param([switch]$SkipImages)
$ErrorActionPreference = 'Stop'
$workspace = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $workspace
$build = Join-Path $workspace '.build/classes'
$runDirectory = Join-Path $workspace ('.build/run-' + [Guid]::NewGuid().ToString('N'))
$evidence = Join-Path $workspace 'report/evidence'
New-Item -ItemType Directory -Force -Path $build,$runDirectory,$evidence | Out-Null
$sources = Get-ChildItem 1,2,3,4,5,6,7,8,9,10,11,verification -Filter *.java | ForEach-Object FullName
& javac --release 17 -Xlint:all -encoding UTF-8 -d $build $sources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
$utf8 = New-Object System.Text.UTF8Encoding($false)

# Exercise order is recorded explicitly; filenames describe the subject and the source comments retain exercise numbers.
$rows = @'
1|1|CarBasics|Create and display three independent car objects.|Toyota|Objects carry their own state and behaviour.
1|2|CarAntique|Identify cars older than 25 years.|Antique\? true|The strict greater-than comparison excludes a car exactly 25 years old.
1|3|LargestRectangle|Find the greatest area among five rectangles.|area=36.0|Comparing calculated areas identifies the 6 by 6 rectangle.
1|4|RectangleAliasing|Predict and verify reference aliasing.|Same object\? true|Changing r2.width also changes r1.width because both references identify one object.
1|5|PersonAddress|Demonstrate composition, chained access and null.|NullPointerException|A chained expression requires every intermediate reference to be non-null.
2|1|CircleConstructors|Use overloaded constructors with a default radius.|Default radius: 1.0|The no-argument constructor delegates to the radius constructor.
2|2|CircleCounter|Count successful circle constructions with static state.|Circles created: 3|The static field belongs to the class and constructor chaining increments it once.
2|3|EmployeeConstructors|Overload employee constructors and copy data into a new identity.|Same object\? false|The copy has equivalent employee data but receives a new object and employee ID.
2|4|InitialisationOrder|Predict and trace static and instance blocks.|static block 2|Static blocks execute once in declaration order; instance blocks execute before each constructor.
2|5|TemperatureFactories|Use named static factories and a private constructor.|32.00 F = 0.00 C|Factory method names distinguish Celsius from Fahrenheit without ambiguous signatures.
3|1|ValidatedStudent|Reject invalid marks through setters.|Marks after rejection: 82|Validation keeps the previous valid state intact after a failed update.
3|2|StudentGrades|Compute A, B, C or F from marks.|100 -> A|The grade is derived from marks and cannot become stale.
3|3|BankAccountHistory|Expose an unmodifiable transaction snapshot.|History modification blocked|List.copyOf prevents both external modification and later changes to a returned snapshot.
3|4|ImmutablePerson|Create a final class with private final fields and no setters.|Farai \(21\)|The actual ImmutablePerson class now enforces the requested immutable design.
3|5|KelvinTemperature|Hide kelvin storage behind conversion methods.|Rejected:|The public factories and getters stay usable even when the internal unit changes.
4|1|PersonStudent|Reuse Person.display from Student.|Student course:|An inherited method prints the common name and age; the subclass adds course state.
4|2|PersonTeacher|Share Person code between Student and Teacher.|Subject: Java|Both subclasses inherit one display implementation.
4|3|VehicleInheritance|Trace multilevel constructor calls.|ElectricCar constructor|Superclass constructors finish before subclass constructors.
4|4|AnimalConstructor|Explain and correct the implicit-super constructor error.|Bingo|The commented failing example needs an explicit call to Animal(String).
4|5|ComposedStack|Replace ArrayList inheritance with stack composition.|Empty\? true|Only stack operations are exposed, so callers cannot edit arbitrary list positions.
5|1|MethodOverloading|Test print overloads for four parameter types.|int\[\]: \[1, 2, 3\]|The declared argument type selects the overload at compile time.
5|2|AnimalPolymorphism|Invoke Cat, Cow and Duck sound methods through Animal references.|Quack|The actual object type chooses the overridden sound method at runtime.
5|3|EmployeePayroll|Add Intern and calculate a mixed employee payroll.|Total payroll: 10150.00|The same payroll loop handles Employee, Manager, Developer and Intern.
5|4|FieldMethodDispatch|Contrast field hiding and method dispatch.|Method through Parent: child|Fields use the reference type while overridden methods use the runtime object type.
5|5|PaymentProcessing|Compare instanceof pattern matching with process dispatch.|Polymorphism:|A new polymorphic payment class can reuse the processor without adding type branches.
6|1|AbstractShapes|Implement abstract area and perimeter contracts.|Square: area=16.00|Concrete shapes fulfil the same abstract contract with different calculations.
6|2|FlyableDemo|Give unrelated classes a shared interface.|Aeroplane flies|An interface describes a shared capability without a shared domain superclass.
6|3|AccountAbstraction|Share deposit logic and specialise monthly fees.|CurrentAccount: balance=150.00|Concrete account types supply the abstract fee while reusing balance management.
6|4|ComparableStudents|Sort students by natural name order and a marks comparator.|By marks ascending:|Comparable supplies the default ordering and Comparator supplies an alternative.
6|5|DefaultMethodConflict|Resolve two conflicting interface default methods.|Both resolves the conflict|The class overrides hello and explicitly chooses defaults with InterfaceName.super.
7|1|SafeDivision|Read integers and handle invalid input or zero divisor.|Result: 4|Successful integer division and both requested exception paths are exercised.
7|2|SafeArrayAccess|Catch array index errors at the access point.|Index -1 is outside|Invalid indexes are reported without terminating the demonstration.
7|3|AgeValidation|Throw a checked exception outside ages 0 to 120.|Invalid age: 121|The checked exception makes validation failure part of the method contract.
7|4|InsufficientFunds|Throw a checked exception instead of returning false.|Balance after rejected withdrawal: 55.0|The exception carries failure details and leaves the account balance unchanged.
7|5|FinallyReturn|Predict finally execution around a return.|returned: 1|The finally block executes before the pending return completes.
8|1|IntegerArrayList|Summarise ten integers and reverse their order.|Sum: 55|ArrayList stores boxed integers and Collections supplies maximum and reversal operations.
8|2|UniqueNames|Remove name duplicates with a Set.|Without duplicates: \[Ana, Ben, Cleo\]|LinkedHashSet also preserves first-occurrence order.
8|3|GenericStack|Build a generic stack with all four operations.|Empty\? true|Type parameters give compile-time safety and empty-stack operations fail clearly.
8|4|WordFrequency|Read and rank case-insensitive word frequencies.|java: 2|A map counts words, then entries sort by decreasing count and alphabetic tie-break.
8|5|GenericBubbleSort|Sort Integer and String arrays with a bounded generic method.|\[1, 1, 2, 3\]|Comparable supplies comparisons; int[] cannot bind to reference-type T[].
9|1|LevelAdvice|Switch over the three enum constants.|HIGH: Take immediate action|The enum restricts input to the defined levels.
9|2|StudentRecord|Print a record, compare equal values and access components.|Equal values\? true|Records generate component accessors and value-based equality.
9|3|BookEquality|Reject duplicate ISBNs in a HashSet.|Duplicate added\? false|Matching equals and hashCode allow correct set membership.
9|4|CustomLinkedList|Use a static Node and inner iterator in a for-each loop.|Exhausted iterator correctly rejects|The list implements Iterable and its iterator obeys the exhaustion contract.
9|5|CoinChange|Return a minimum set of US coins.|41 cents:|Greedy change is checked against an independent minimum-coin calculation for amounts 0-200.
10|1|FileLines|Save five user-entered lines and read them back.|Read back from|All five lines are round-tripped to UTF-8 text without creating a partial file on short input.
10|2|RemoveEvenNumbers|Use an explicit Predicate with removeIf.|Odd numbers: \[-1, 1, 3, 5\]|The predicate removes even numbers including zero and negative evens.
10|3|FileStatistics|Count lines, words and Unicode characters in a text file.|Words: 9|The report states whether line-ending characters are included in the count.
10|4|AboveAverageStudents|Use streams to select names above average alphabetically.|Class average: 75.00|Average, filtering, mapping and sorting are separate stream operations.
10|5|LearnerReports|Load CSV, group learners by grade band and save three reports.|Band A: 2 learners|Collectors.groupingBy produces groups, and one report is written for each A/B/C band.
11|1|SingletonFactoryDemo|Demonstrate Singleton and Factory patterns.|Same instance\? true|A private constructor controls shared configuration and a factory hides concrete notification creation.
11|2|StrategyObserverDemo|Demonstrate Strategy and Observer patterns.|Advisor alert: Databases failed|A strategy changes checkout behaviour and observers receive grade events.
'@ -split "`n" | Where-Object { $_.Trim() } | ForEach-Object {
    $parts = $_.TrimEnd("`r").Split('|')
    [pscustomobject]@{ Lab=[int]$parts[0]; Exercise=[int]$parts[1]; Class=$parts[2]; Objective=$parts[3]; Expected=$parts[4]; Conclusion=$parts[5] }
}
$records = [System.Collections.Generic.List[object]]::new()
function Invoke-RecordedRun {
    param([string]$Class, [string]$Label, [string[]]$Arguments=@(), [string]$InputText='', [string]$Expected='', [string]$WorkingDirectory=$workspace)
    $info = New-Object System.Diagnostics.ProcessStartInfo
    $info.FileName = (Get-Command java).Source
    $quoted = @('-Dfile.encoding=UTF-8', '-cp', ('"' + $build + '"'), $Class)
    foreach ($argument in $Arguments) { $quoted += '"' + $argument + '"' }
    $info.Arguments = $quoted -join ' '
    $info.WorkingDirectory = $WorkingDirectory
    $info.UseShellExecute=$false; $info.CreateNoWindow=$true
    $info.RedirectStandardInput=$true; $info.RedirectStandardOutput=$true; $info.RedirectStandardError=$true
    $info.StandardOutputEncoding=$utf8; $info.StandardErrorEncoding=$utf8
    $process=New-Object System.Diagnostics.Process
    $process.StartInfo=$info
    [void]$process.Start()
    $stdout=$process.StandardOutput.ReadToEndAsync(); $stderr=$process.StandardError.ReadToEndAsync()
    $process.StandardInput.Write($InputText); $process.StandardInput.Close()
    if (!$process.WaitForExit(20000)) { $process.Kill(); throw "Timed out: $Label" }
    $output=$stdout.Result + $stderr.Result
    if ($process.ExitCode -ne 0) { throw "$Label failed with exit $($process.ExitCode): $output" }
    if ($Expected -and $output -notmatch $Expected) { throw "$Label did not match expected output $Expected : $output" }
    $command = 'java ' + $Class + ' ' + ($Arguments -join ' ')
    $transcript = "$command`n"
    if ($InputText) { $transcript += "[standard input]`n$InputText[program output]`n" }
    $transcript += $output
    $log=Join-Path $evidence ($Label + '.txt')
    [IO.File]::WriteAllText($log,$transcript,$utf8)
    if (!$SkipImages) {
        & java '-Djava.awt.headless=true' -cp $build OutputCapture $log (Join-Path $evidence ($Label + '.png')) $Label
        if ($LASTEXITCODE -ne 0) { throw "Image rendering failed: $Label" }
    }
    $records.Add([pscustomobject]@{Label=$Label;Class=$Class;Output=$output;Command=$command;Input=$InputText})
    Write-Host "PASS: $Label"
    return $output
}

foreach ($row in $rows) {
    $arguments=@(); $inputText=''; $workingDirectory=$workspace
    if ($row.Class -eq 'SafeDivision') { $inputText="8 2`n" }
    if ($row.Class -eq 'WordFrequency') { $inputText="Java is fun. Java is useful!`n" }
    if ($row.Class -eq 'FileLines') { $arguments=@((Join-Path $runDirectory 'five-lines.txt')); $inputText="one`ntwo`nthree`nfour`nfive`n" }
    if ($row.Class -eq 'FileStatistics') { $arguments=@((Join-Path $workspace '10/sample.txt')) }
    if ($row.Class -eq 'LearnerReports') { $arguments=@((Join-Path $workspace '10/learners.csv'),(Join-Path $runDirectory 'grade-reports')) }
    $label=('{0:D2}-{1:D2}-{2}' -f $row.Lab,$row.Exercise,$row.Class)
    $null=Invoke-RecordedRun -Class $row.Class -Label $label -Arguments $arguments -InputText $inputText -Expected $row.Expected
}
$null=Invoke-RecordedRun SafeDivision '07-01-DivisionByZero' -InputText "8 0`n" -Expected 'Cannot divide by zero'
$null=Invoke-RecordedRun SafeDivision '07-01-NonNumericInput' -InputText "hello 2`n" -Expected 'two valid integers'
$null=Invoke-RecordedRun FileLines '10-01-ShortInput' -Arguments @((Join-Path $runDirectory 'short.txt')) -InputText "only one`n" -Expected 'No file written'
if (Test-Path (Join-Path $runDirectory 'short.txt')) { throw 'Short input unexpectedly wrote a file' }
$roundTrip=Get-Content -LiteralPath (Join-Path $runDirectory 'five-lines.txt')
if (($roundTrip -join ',') -ne 'one,two,three,four,five') { throw 'Five-line persistence content differs' }
$null=Invoke-RecordedRun FileStatistics '10-03-MissingFile' -Arguments @((Join-Path $runDirectory 'missing.txt')) -Expected 'Cannot read file'
$null=Invoke-RecordedRun LearnerReports '10-05-MissingCsv' -Arguments @((Join-Path $runDirectory 'missing.csv'),(Join-Path $runDirectory 'missing-reports')) -Expected 'Report generation failed'
foreach ($band in 'A','B','C') { if (!(Test-Path (Join-Path $runDirectory "grade-reports/report-$band.txt"))) { throw "Missing band $band report" } }
$null=Invoke-RecordedRun ExerciseChecks 'ExerciseBoundaryChecks' -Expected 'ALL \d+ EXERCISE CHECKS PASSED'
$null=Invoke-RecordedRun LibraryTests 'LibraryChecks' -Arguments @((Join-Path $runDirectory 'library-tests')) -Expected 'ALL 26 LIBRARY CHECKS PASSED'
$libraryDirectory=Join-Path $runDirectory 'library-menu'
$menu="1`nBOOK`nB1`nJava, Objects`nAda`n1`nDVD`nD1`nJava on Film`n90`n2`nM1`nRudo`n3`nM1`nB1`n3`nM1`nB1`n6`nJAVA`n5`n0`n"
$null=Invoke-RecordedRun LibraryApp 'LibraryMenuFirstRun' -Arguments @($libraryDirectory) -InputText $menu -Expected 'Library saved'
$output=Invoke-RecordedRun LibraryApp 'LibraryMenuReload' -Arguments @($libraryDirectory) -InputText "7`n4`nB1`n0`n" -Expected 'Loaded 2 items, 1 members, 1 active loans'
if ($output -notmatch 'Item returned') { throw 'Menu return was not completed' }
$null=Invoke-RecordedRun LibraryApp 'LibraryMenuReturnPersisted' -Arguments @($libraryDirectory) -InputText "0`n" -Expected 'Loaded 2 items, 1 members, 0 active loans'

# Generate a report from executed evidence, not invented expected output.
$html=[System.Text.StringBuilder]::new()
[void]$html.AppendLine(@'
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Java OOP Lab Report</title>
<style>body{font:16px/1.6 system-ui,sans-serif;color:#172033;background:#f1f5f9;margin:0}main{max-width:1100px;margin:32px auto;background:white;padding:40px;border-radius:16px}h1{font-size:36px;line-height:1.2}h2{border-bottom:2px solid #e2e8f0;padding-bottom:8px;margin-top:40px}summary{cursor:pointer;font-weight:650;padding:12px;background:#eff6ff;border-radius:8px}details{margin:18px 0;border:1px solid #dbeafe;padding:8px;border-radius:10px}details>div{padding:14px}img{width:100%;height:auto;border-radius:8px}pre{white-space:pre-wrap;overflow-wrap:anywhere;background:#f8fafc;padding:16px;font:13px/1.6 monospace}a{color:#175ac5}small{color:#475569}.status{padding:14px;background:#dcfce7;border-radius:10px}nav a{margin-right:14px}table{border-collapse:collapse;width:100%}th,td{padding:8px;text-align:left;border-bottom:1px solid #e2e8f0}@media print{body{background:white}main{margin:0;padding:0}details{break-inside:avoid}summary{background:white}nav{display:none}}</style></head><body><main>
<h1>Object-Oriented Programming in Java</h1><p>Lab report: Labs 1–10, design pattern demonstrations, and the Library Management System mini-project.</p>
<p class="status">Verification passed: all 50 exercises ran successfully, Java 17 compilation passed, and boundary, error-handling and library persistence checks passed.</p>
<p>All outputs below come from actual Java executions. Images are rendered console-output captures, not desktop screenshots. Source code is linked for each exercise and includes explanations.</p>
'@)
[void]$html.AppendLine('<nav>' + ((1..11 | ForEach-Object {"<a href='#lab$_'>Lab $_</a>"}) -join ' ') + '</nav>')
foreach ($lab in 1..11) {
    [void]$html.AppendLine("<h2 id='lab$lab'>Lab $lab</h2>")
    foreach ($row in ($rows | Where-Object Lab -eq $lab)) {
        $label=('{0:D2}-{1:D2}-{2}' -f $row.Lab,$row.Exercise,$row.Class)
        $record=$records | Where-Object Label -eq $label | Select-Object -First 1
        $encode={param($value) [System.Net.WebUtility]::HtmlEncode([string]$value)}
        $objective=&$encode $row.Objective; $conclusion=&$encode $row.Conclusion
        $sourcePath=Join-Path $workspace "$lab/$($row.Class).java"
        $source=&$encode (Get-Content -Raw -LiteralPath $sourcePath)
        [void]$html.AppendLine("<details open><summary>Exercise $($row.Exercise): $($row.Class)</summary><div><p><b>Objective:</b> $objective</p><p><a href='../$lab/$($row.Class).java'>Java source</a> · <a href='evidence/$label.txt'>Execution transcript</a></p>")
        if (!$SkipImages) { [void]$html.AppendLine("<img src='evidence/$label.png' alt='Recorded output of $($row.Class)' loading='lazy'>") }
        else { [void]$html.AppendLine('<pre>' + (&$encode $record.Output) + '</pre>') }
        [void]$html.AppendLine("<p><b>Conclusion:</b> $conclusion</p><details><summary>Source code</summary><pre>$source</pre></details></div></details>")
    }
}
[void]$html.AppendLine('<p>The complete mini-project design, requirements mapping and assumptions are in <a href="../11/PROJECT_REPORT.md">PROJECT_REPORT.md</a>. The <a href="../11/CLASS_DIAGRAM.md">class diagram</a> documents relationships.</p><h2>Additional validation evidence</h2>')
foreach ($record in ($records | Where-Object {$_.Label -notmatch '^\d\d-\d\d-(Car|Largest|Rectangle|Person|Circle|Employee|Initial|Temperature|Validated|Student|Bank|Immutable|Kelvin|Vehicle|Animal|Composed|Method|Field|Payment|Abstract|Flyable|Account|Comparable|Default|Safe|Age|Insufficient|Finally|Integer|Unique|Generic|Word|Level|Book|Custom|Coin|File|Remove|Above|Learner|Singleton|Strategy)'})) {
    [void]$html.AppendLine("<details><summary>$($record.Label)</summary><div><a href='evidence/$($record.Label).txt'>Transcript</a><pre>" + [System.Net.WebUtility]::HtmlEncode($record.Output) + '</pre></div></details>')
}
[void]$html.AppendLine('<p><b>Assumptions:</b> Lab 3 grades use A ≥75, B ≥60, C ≥50, otherwise F. Lab 10 GPA bands use A ≥3.5, B ≥3.0, otherwise C. Library books loan for 14 days and DVDs for 7 days. Due today is not overdue. These cutoffs and loan periods are choices where the manual leaves values unspecified.</p><p><b>Conclusion:</b> The assignment demonstrates encapsulation, inheritance, polymorphism and abstraction, then combines them with generics, collections, exceptions, file I/O, streams and persistent library workflows.</p></main></body></html>')
[IO.File]::WriteAllText((Join-Path $workspace 'report/LAB_REPORT.html'),$html.ToString(),$utf8)
$summary=@{javaSources=$sources.Count; exercises=50; patternDemos=2; recordedRuns=$records.Count; javaTarget=17; passed=$true; generatedAt=(Get-Date).ToString('o')}
[IO.File]::WriteAllText((Join-Path $evidence 'verification-summary.json'),($summary | ConvertTo-Json),$utf8)
Write-Host "ALL CHECKS PASSED: $($records.Count) recorded executions; report generated."
