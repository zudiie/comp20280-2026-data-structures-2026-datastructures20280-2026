<h2> Q2 </h2>

Algorithm countExternal(p): <br>
    if p is null:<br>
        return 0<br>
    if left(p) == null and right(p) == null:<br>
        return 1<br>
    else:<br>
        return countExternal(left(p)) + countExternal(right(p)) <br>


<h2> Q3 </h2>

Algorithm countLeftExternal(p):<br>
count = 0<br>
if left(p) is not null:<br>
if isLeaf(left(p)):<br>
count = count + 1<br>
else:<br>
count = count + countLeftExternal(left(p))<br>
if right(p) is not null:<br>
count = count + countLeftExternal(right(p))<br>
return count<br>

<h2> Q7 </h2>

Algorithm countDescendants(p):<br>
return size(p) - 1<br>
<br>
Algorithm size(p):<br>
if p is null:<br>
return 0<br>
return 1 + size(left(p)) + size(right(p))<br>