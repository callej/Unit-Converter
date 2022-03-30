package converter

enum class Units(val type: String, val uName: List<String>,
                 val singular: String, val plural: String, val from: (Double) -> Double, val to: (Double) -> Double) {
    M("Length", listOf("m", "meter", "meters"),
        "meter", "meters", { x -> x }, { x -> x }),
    KM("Length", listOf("km", "kilometer", "kilometers"),
        "kilometer", "kilometers", { x -> 1000 * x }, { x -> x / 1000 }),
    CM("Length", listOf("cm", "centimeter", "centimeters"),
        "centimeter", "centimeters", { x -> 0.01 * x }, { x -> x / 0.01 }),
    MM("Length", listOf("mm", "millimeter", "millimeters"),
        "millimeter", "millimeters", { x -> 0.001 * x }, { x -> x / 0.001 }),
    MI("Length", listOf("mi", "mile", "miles"),
        "mile", "miles", { x -> 1609.35 * x }, { x -> x / 1609.35 }),
    YD("Length", listOf("yd", "yard", "yards"),
        "yard", "yards", { x -> 0.9144 * x }, { x -> x / 0.9144 }),
    FT("Length", listOf("ft", "foot", "feet"),
        "foot", "feet", { x -> 0.3048 * x }, { x -> x / 0.3048 }),
    IN("Length", listOf("in", "inch", "inches"),
        "inch", "inches", { x -> 0.0254 * x }, { x -> x / 0.0254 }),
    G("Weight", listOf("g", "gram", "grams"),
        "gram", "grams", { x -> x }, { x -> x }),
    KG("Weight", listOf("kg", "kilogram", "kilograms"),
        "kilogram", "kilograms", { x -> 1000 * x }, { x -> x / 1000 }),
    MG("Weight", listOf("mg", "milligram", "milligrams"),
        "milligram", "milligrams", { x -> 0.001 * x }, { x -> x / 0.001 }),
    LB("Weight", listOf("lb", "pound", "pounds"),
        "pound", "pounds", { x -> 453.592 * x }, { x -> x / 453.592 }),
    OZ("Weight", listOf("oz", "ounce", "ounces"),
        "ounce", "ounces", { x -> 28.3495 * x }, { x -> x / 28.3495 }),
    C("Temp", listOf("degree celsius", "degrees celsius", "celsius", "dc", "c"),
        "degree Celsius", "degrees Celsius", { x -> x }, { x -> x }),
    F("Temp", listOf("degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df", "f"),
        "degree Fahrenheit", "degrees Fahrenheit", { x -> (x - 32) * 5 / 9 }, { x -> x * 9 / 5 + 32 }),
    K("Temp", listOf("kelvin", "kelvins", "k"),
        "kelvin", "kelvins", { x -> x - 273.15 }, { x -> x + 273.15 }),
    NULL("", listOf(""),
        "", "", { 0.0 }, { 0.0 }),
    ERROR("Parse error", listOf(""),
        "", "", { 0.0 }, { 0.0 });

    companion object {
        fun contains(unitName: String): Units {
            for (unit in values()) {
                if (unitName in unit.uName) return unit
            }
            return NULL
        }
    }
}

fun parse(msg: List<String>): List<Units> {
    if (msg.size < 4 + msg.count { Regex("degrees?").matches(it) }) return listOf(Units.ERROR, Units.ERROR)
    val fromUnit: Units
    val toUnit: Units
    var next = 1
    if (Regex("degrees?").matches(msg[next])) {
        fromUnit = Units.contains(msg[next] + " " + msg[next + 1])
        next += 3
    } else {
        fromUnit = Units.contains(msg[next])
        next += 2
    }
    if (Regex("degrees?").matches(msg[next])) {
        toUnit = Units.contains(msg[next] + " " + msg[next + 1])
    } else {
        toUnit = Units.contains(msg[next])
    }
    return listOf(fromUnit, toUnit)
}

fun main() {
    while (true) {
        print("\nEnter what you want to convert (or exit): ")
        val msg = readln().lowercase().split(" ")
        if (msg[0] == "exit") break
        val (fromUnit, toUnit) = parse(msg)
        if (fromUnit != Units.ERROR && Regex("[+-]?\\d+(\\.\\d+)?").matches(msg[0])) {
            if (fromUnit != Units.NULL && toUnit != Units.NULL && fromUnit.type == toUnit.type) {
                val value = msg[0].toDouble()
                if (value < 0 && (fromUnit.type == "Length" || fromUnit.type == "Weight")) {
                    println("${fromUnit.type} shouldn't be negative")
                    continue
                }
                val fromUnitString = if (value == 1.0) fromUnit.singular else fromUnit.plural
                val convertedValue = toUnit.to(fromUnit.from(value))
                val toUnitString = if (convertedValue == 1.0) toUnit.singular else toUnit.plural
                println("$value $fromUnitString is $convertedValue $toUnitString")
            } else {
                val fromUnitString = if (fromUnit == Units.NULL) "???" else fromUnit.plural
                val toUnitString = if (toUnit == Units.NULL) "???" else toUnit.plural
                println("Conversion from $fromUnitString to $toUnitString is impossible")
            }
        } else {
            println("Parse error")
        }
    }
}
