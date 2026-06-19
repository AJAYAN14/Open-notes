val line = "- [ ] Buy milk"
val regex = Regex("^([\\-*]\\s*)?\\[[ xX]]( .*)?")
println("Matches: " + line.matches(regex))

val line2 = "[ ] Buy milk"
println("Matches2: " + line2.matches(regex))

val line3 = "* [x] Buy milk"
println("Matches3: " + line3.matches(regex))
