# Omakase - A Language For Web Development in the Large

Web development has evolved significantly over the years. JavaScript was originally targeted for simple tasks like
validating form input where a significant program was anything over a few lines of code. Today web applications
can include millions of lines of code with hundreds of developers.

As projects grow, the needs of developers change. What was once convenient for small projects becomes unmanageable for
large teams. Omakase takes a fresh look at a web development language for large projects.

## Goals

Omakase is designed for programming web apps in the large. Specifically the goals of Omakase are:

* Modern
    * Includes all the features you expect in a modern language:
    * All Strongly Typed, all the time
        * APIs must be explicitly typed
        * locals are type inferred
        * no falling off the type system
    * Objects
      * Access control(public/protected/private)
      * Traits/mixins/interfaces/multiple base classes
      * Class extensions for code generation
      * Explicit abstract/overridable/override
      * Nested classes
    * Explicit Nullable type. Objects are non-null by default.
    * First class Functions
    * Generics
        * sadly un-reified
        * variance
    * Primitive Types
        * compatible with JS
        * number - 64 bit float
        * string - array of 16 bit code units
        * bool
        * Nullable<T>
        * int - 32-bit integer
    * arrays
    * enums
    * Namespaces
    * Async/Await
    * Generators(yield)
    * Sane iteration (for/in)
    * Annotations
    * JSON supported via data(aka anonymous) classes (with structural sub-typing)?
    * Immutable by default?
    * Pattern matching?
* Target
    * JavaScript is the only target runtime environment
* Performance
    * ~~As good or~~ better runtime performance than hand written JS.
* Developer experience
    * IDE experience as good as your favorite language (Java, Kotlin, C#, Swift, Scala, ...)
* Tooling
    * Fast, scalable build tools
    * Instant edit/reload dev cycle
* Libraries
    * support and encourage rich libraries, including cross library optimizations.
    * Using libraries is pay as you go, using a single function from a large library should only pull in that single function into the output.
* Deployment
    * TODO: Support bundling the app into downloadable deployment units
* Familiarity
    * low learning curve for developers coming from existing JS dialects
* Support for the Browser through an opinionated HTML/CSS library: React/Relay?

### Non-Goals

* supporting all JS idioms
    * Omakase is JavaScript, the good parts, plus more.
* interoperate with existing JS libraries

## Current Status

Currently prototype quality. The end-to-end compiler is working, including both debug and a (simple) optimizer.


## FAQ

### Why not {TypeScript, FlowJS}?

TypeScript and Flow both carry over too much legacy from JavaScript. By supporting existing, untyped JS libraries
(including the builtin JS library) it is too easy to accidentally fall off the type system. Also, neither addresses
the packaging/deployment issues, and the JS legacy makes significant improvements in that area unlikely in the foreseeable future.
See https://github.com/Microsoft/TypeScript/issues/1151 for a discussion of the issues.

### Why not {Java, Kotlin, C#, Scala}?

While these languages are well designed and have great tooling, they cannot be made to target the Web (JavaScript VMs)
without intolerable performance and/or incompatibility.
