# 🧪 Using Kotest with WordSpec in Kotlin

Welcome to the world of Kotest and WordSpec — where tests read like natural language, and your test suite becomes a conversation between you and your code.

This guide is tailored for **WordSpec** style, helping you write elegant, structured, and expressive tests using **Kotest**.

---

## 📦 Setup

Make sure you’ve added Kotest and MockK (if mocking is needed) in your `build.gradle.kts`:

```kotlin
dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:latest") // Core runner
    testImplementation("io.kotest:kotest-assertions-core:latest") // Matchers
    testImplementation("io.mockk:mockk:latest") // Mocking framework (optional)
}
````

Enable JUnit 5 in the module-level `build.gradle.kts`:

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## 📝 What is WordSpec?

**WordSpec** is a Kotest style that lets you write tests like natural language:

```kotlin
class CalculatorTest : WordSpec({

    "A calculator" should {
        "add two numbers correctly" {
            // test code here
        }

        "subtract two numbers correctly" {
            // test code here
        }
    }
})
```

It reads like a sentence:

> "A calculator should add two numbers correctly."

Perfect for **BDD** and making your test output human-readable.

---

## 🧪 Writing Tests with WordSpec

### ✅ Basic Test

```kotlin
class MathTest : WordSpec({
    "The math utility" should {
        "return 4 when adding 2 and 2" {
            val result = 2 + 2
            result shouldBe 4
        }
    }
})
```

### ✅ Using Matchers

Kotest gives you powerful matchers:

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

"A string" should {
    "contain the expected value" {
        "hello kotest" shouldContain "kotest"
    }
}
```

### ✅ Grouping Scenarios

You can group tests logically using nesting:

```kotlin
"An API response" should {
    "return success" {
        // test success case
    }

    "return error" {
        // test error case
    }
}
```

---

## 🧪 Before/After Hooks

Need setup or teardown? Use Kotest lifecycle methods:

```kotlin
override fun beforeTest(testCase: TestCase) {
    println("Before each test")
}

override fun afterTest(testCase: TestCase, result: TestResult) {
    println("After each test")
}
```

Or inside your `WordSpec` block:

```kotlin
beforeTest {
    // setup logic
}

afterTest {
    // teardown logic
}
```

---

## 🧪 Using MockK with WordSpec

MockK plays beautifully with Kotest:

```kotlin
class ServiceTest : WordSpec({

    val service = mockk<MyService>()

    "MyService" should {
        "return correct data" {
            every { service.fetchData() } returns "Mocked Data"

            service.fetchData() shouldBe "Mocked Data"

            verify { service.fetchData() }
        }
    }
})
```

---

## Running tests
- To run all tests in the project: `./gradlew test` (On Windows, use `gradlew.bat test`)
- To run tests for a specific module (e.g., app module): `./gradlew :app:test`
- To run a specific test class: `./gradlew :app:test --tests "com.example.CalculatorTest"`
- To run a specific test method:
  - `./gradlew :app:test --tests "com.example.CalculatorTest.A calculator should add two numbers correctly"`
  - `./gradlew :app:test --tests "com.example.CalculatorTest#testAddTwoNumbersCorrectly"`

---

## 📚 Best Practices

* ✅ Write your test names like sentences.
* ✅ Group related scenarios under a common `"subject" should { }`.
* ✅ Use `shouldBe`, `shouldContain`, and other Kotest matchers for clarity.
* ✅ Avoid duplicating setup logic — use `beforeTest`.
* ✅ Keep tests small and focused.

---

## ✅ When to Use WordSpec

Choose **WordSpec** when:

* You want your tests to **read like documentation**.
* You prefer **natural language** and **BDD** structure.
* You care about the readability of test output and reports.

Example output:

```
A calculator
  ✓ should add numbers correctly
  ✓ should subtract numbers correctly
```

---

## 📌 Summary

| Feature       | WordSpec Style                         |
| ------------- | -------------------------------------- |
| Structure     | Nested: `"X" should { … }`             |
| Readability   | 🟢 Excellent                           |
| BDD Support   | 🟢 Native support                      |
| Best Use Case | Complex features, business rules, APIs |
| Test Output   | Hierarchical & natural                 |

---

## 💡 Extra Tips

* For shared setup per group, consider `beforeEach` inside a `should` block.
* Don’t over-nest — two levels (`"X" should { "Y" { … }}`) is often enough.
* Combine with `DescribeSpec` if you want even more structured BDD.

---

## 🚀 You’re Ready!

With WordSpec, you can build test suites that are:

* Easy to read ✅
* Easy to extend ✅
* Easy to trust ✅

Write your tests like you’re telling a story — because with **WordSpec**, you can.

Need examples or templates? Just ask!
