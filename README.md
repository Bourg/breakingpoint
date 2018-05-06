BreakingPoint
=============

BreakingPoint is a system for determining what point in your code is the reason for test case failures. It currently sucks and hardly works but at least it works for instrumenting bytecode...

Building
--------

The build is managed by Maven. Since this is an agent, it is probably best to build it all into a single comprehensive jar:

```
mvn package assembly:single
```

Attaching
---------

BreakingPoint is a Java Agent and can be attached at runtime:

```
java ... -javaagent:path/to/breakingpoint.jar ...
```

You should of course replace the jarfile name with the path to wherever you keep your copy.

Interpreting Output
-------------------

All BreakingPoint output is preceeded by a prefix of the form `<<BREAKING_POINT>> [THREAD \d+]: `. When interpreting output, be sure to isolate analysis to relevant threads. Different runtime environments may have all kinds of errant threads that have nothing to do with the execution of user code.

In its current state, BreakingPoint outputs information about the following events (note that all class names are fully qualified and use `.` as their delimiter, NOT `/`):
- Call (directly before an invoke instruction)
    - Format: `--> <caller> <callee>`
- Return (directly following an invoke instruction, NOT directly before the return instruction)
    - Format: `<-- <callee>`
- Branching
    - Pre-branch: `<-> <method> branch <branch number>?`
    - Branch false: `Did not branch!`

Note that branching outputs before a branch and after a branch is not taken. Since there is no guarantee that there is a 1:1 relationship between jump targets and branch instructions, it is not safe to associate the target statically with the branch instruction. It would certainly be possible to leverage a local variable to track the jump number dynamically, but this is a 1 credit class.
