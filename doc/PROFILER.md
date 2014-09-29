Enabling the graph profiler in Eclipse
======================================

DNA has a profiling extension that is able to count accesses to the data structures that
hold edge and node lists. It can be activated using AspectJ. This document will guide you
through this process.

1) Install AspectJ
------------------
AspectJ can be installed through the URLs provided at http://www.eclipse.org/ajdt/downloads/.
Add the matching for your Eclipse version through Help -> Install new software. Install
the "AspectJ Development Tools" and restart Eclipse.

2) If necessary: convert DNA project to AspectJ project
-------------------------------------------------------
For that the profiler is able to do its job, the current DNA project needs to be converted
from a pure Java project to an AspectJ project. This is done by right-clicking the project
in Eclipse and selecting Configure -> Convert to AspectJ project

3) Use the profiler
-------------------
Through calling
	Config.overwrite("PROFILER_ACTIVATED", "true");
in an AspectJ enabled project, the profiler will be used in your program. 


Note: doing only one of the options "Install AspectJ" or "Activating the profiler"
	(Step 3) will not do anything. If only AspectJ is installed, the profiler is weaved
	into the code, but will not actually count calls; if only the profiler is activated,
	the profiler is not weaved into the code and can not count calls.
