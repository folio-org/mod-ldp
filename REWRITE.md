# Possible rewrite in Node or Go

<!-- md2toc -l 2 REWRITE.md -->
* [Background](#background)
* [The work](#the-work)
    * [Work to be done](#work-to-be-done)
    * [Work not to be done](#work-not-to-be-done)
* [Node or Go?](#node-or-go)
    * [Performance](#performance)
    * [TC acceptance](#tc-acceptance)
    * [Unity of code](#unity-of-code)
    * [Developer familiarity](#developer-familiarity)



## Background

mod-ldp is not a large or complex piece of code: even though it's in the verbose language Java (using the Spring framework), the current version comes to only 1578 lines of code. It was originally written by a short-term hire who has since been lost to the project. Arguably no-one feels much affection or sense of ownership towards it.

At present the mod-ldp code it is maintained by Kurt, who is consistently overloaded. None of us likes the Spring framework. If we rewrote mod-ldp in a different language, the burden of maintenance and further development could be passed on to Mike. We would also rid ourselves of Spring. Having the UI+backend stack all owned by Mike would simplify development schedules.

For these reasons, we are considering a rewrite in either Node or Go.


## The work


### Work to be done

Whichever reimplementation language is chosen, we will need to consider the following areas of work. The _very rough_ time estimates given here are for a rewrite in Node, which Mike is very familiar with.

* New code to read configuration file `src/main/resources/application.yml`
-- **1 day**
* New code to access LDP/MetaDB PostgresQL database
-- **2 days**
* New code to Access reports stored in GitHub (may just be a simple GET)
-- **1/2 day**
* Rewrite the actual code, fulfilling the currently defined WSAPI:
  * Web server loop and asynchronous handlers
  -- **2 days**
  * `/config` and `/config/{key}` endpoints
  -- **2 days**
  * `/db/tables` and `/db/columns` endpoints
  -- **1 day**
  * `/query` endpoint
  -- **2 days**
  * `/reports` endpoint
  -- **1 day**
* Write new tests for the code
-- **5 days**
* Establish a linting mechanism (replacing present use of checkstyle)
-- **1/2 day**
* Modify Dockerfile
-- **1/2 day**
* Modify existing GitHub workflows (doc, lint, schema-lint)
-- **1/2 day** but most likely by a DevOps person
* MAYBE add new GitHub workflows for CI and releases. (It's not clear how this is done at present)
-- **1 day** but most likely by a DevOps person
* Modify Jenkinsfile is wanted, or discard if superseded by GitHub Actions
-- **1/2 day**
* Rewrite top-level README.md and minor modificationd to documentation
-- **1/2 day**

Total **20 days** of which 1.5 will be DevOps

About three weeks, then.


### Work not to be done

The following should not need work:
* The RAMLs and JSON Schemas should remain identical, so that the WSAPI is plug-compatiblle. (Surprisingly, under [semantic versioning](https://semver.org/) a rewrite in a new language could be released as a new minor version or even a patch-level release!)
* The scripts should not need modifing, as they use WSAPIs

It would be reasonable to more or less double the time estimates for a rewrite in Go, which Mike is less familiar with and which our DevOps people have had to support much less.



## Node or Go?


### Performance

The main reason a typical project might choose Go over Node would be the performance of the compiled code. For CPU-bound code, this could be significant. For some something like this thin layer, which will spend all its time doing network IO and waiting on databases, it will be negligible.


### TC acceptance

We no not know how much difficulty we may run into having the updated mod-ldp accepted into the FOLIO flower releases. At worst, we may have to run through the whole TC module-approval process again. Whether or not that proves necessary, Node is one of the languages already accepted for back-end modules in FOLIO, but Go is not. We could possibly get that changed, but it would be additional work.


### Unity of code

The Report app in FOLIO is made up of three layers:
1. The UI, written in Node
2. mod-ldp, currently written in Java
3. LDP Classic or MetaDB, written in Go

If mod-ldp were rewritten in either Node or Go, it would reduce the number of languages in the stack from 3 to 2. This would be a welcome simplification either way, but does not provide a reason to prefer either language over the other.


### Developer familiarity

At present, Index Data have decent expertise in Node: it's Mike's and Jason's primary programming language now (Jason wrote mod-graphql in it and Mike took over development/maintenance; Mike wrote ReShare's OpenURL listener in it and Jason took over development/maintenance) and Niels-Erik also have some experience -- plus there is Michal if necessary.

We have less expertise in Go: only Nassib is familiar with it, and Mike has written one program in it. On the other hand, given that MetaDB is written in Go, arguably we need to invest in getting more up to speed with it.


