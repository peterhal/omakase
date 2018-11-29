# Omakase - A Language For Web Development in the Large

There are currently a lot of languages available for developing apps targeting the web. Existing languages have all
focused on incremental migration from existing JS code. Support and interop with legacy JS code has aided adoption.
Existing choices drag with them a significant amount of JavaScript legacy and technical debt. Existing languages have
not made significant strides forwards in addressing many of JS's inherent weaknesses in programming in the large.

### Goals

Omakase is designed for programming web apps in the large. Specifically the goals of Omakase are:

* Target
    * JavaScript is the target runtime environment
    * may monkey patch the builtins, not intended to be compatible with existing JS libs
* Performance
    * ~~As good or~~ better runtime performance than hand written JS.
    * startup time
    * code size
    * Optimizable in both the speed and space axes
* Developer experience
    * strongly typed
    * non-null by default
    * immutable by default?
    * tooling, IDE, Debugger, formatter, etc.
    * support for integrating generated and human authored code
* Development in the large
    * Fast, scalable build tools
    * support and encourage rich libraries, including cross library optimizations.
    * Using libraries is pay as you go, using a single function from a large library should only pull in that single function into the output.
* Deployment - support bundling the app into downloadable deployment units
* Familiarity
    * low learning curve for developers coming from existing JS dialects
* Support for the Browser through an HTML/CSS library: React/Relay.

### Non-Goals

* interoperate with untyped JS
* supporting all JS idioms
* interoperate with existing JS libraries

## Design

### Language

* Primitives - compatible with JS
    * Int32 - only 'special' type which cannot be extended
    * Number (aka Float64)
    * String are array of 16-bit code units
    * Int64 is library type
    * dynamic - the untyped type
* Ref vs. Value Types?
* Classes
    * sealed by default
    * multiple base classes allowed
    * Extension classes
* Reference types are non-nullable
* Option type instead of null/undefined?
*

### Tools

* Basics
    * Diagnostics
    * Formatting
    * Outline view
    * Automated Code-mods
    * Goto-def
    * Find refs
    * Autocomplete
    * IDE Integration
* Extensible tool chain

### Libraries

* A library contains a set of source files, and a set of referenced libraries
* Within a library files may reference each other
* Library dependencies must be an acyclic digraph
* Build tools produce an interface file, as well as output artifacts. If the interface file hasn't changed, then downstream libraries needn't be rebuilt.
* Debug builds are fast
    *  1:1 translation to JS
    * Could be done in browser, similar to babel



## FAQ

### Why not {TypeScript, FlowJS}?

TypeScript and Flow both carry over too much legacy from JavaScript. By supporting existing, untyped JS libraries
(including the builtin JS library) it is too easy to accidentally fall off the type system. Also, neither addresses
the packaging/deployment issues, and the JS legacy makes significant improvements in that area possible in the foreseeable future.

### Why not {Java, Kotlin, C#}?

While these languages are well designed, and have great tooling, they cannot be made to target the Web (JavaScript VMs)
without intolerable performance and/or compatibility.
