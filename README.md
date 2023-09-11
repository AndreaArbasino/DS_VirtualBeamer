# Distributed systems - Virtual beamer

This is the project for the course "Distributed system" (DS), held at [Politecnico di Milano](https://www.polimi.it/).

---

### Project Description
You are to implement in Java a virtual beamer to share a set of slides and show them in a synchronous way under control of a single node (the leader).

### Requirements
Users that want to share their presentations create a new session. Other nodes join one of the available sessions (there could be more sessions led by different users). When the session creator decides the presentation to share (jpg format) the file/files are sent to the joined nodes, then the presentation may start. To send the presentation from the session creator to the other nodes, choose a solution that minimizes network traffic.

Users/nodes may also join while the presentation is running. In this case they choose the node from which to download the presentation in a way to share the load. 

The slide to show is decidedd by the session leader (initially the session creator/owner). During a session, the leader may pass its role to another user, which becomes the new leader. 

### Assumptions
You can assume a LAN scenario (i.e., link-layer broadcast is available). Assume also that the nodes (including the actual leader) may crash, in such a case the leader becomes the session creator, or the leader is elected if the session creator also crashed. 

In general, privilege a "plug&play" solution that leverages the LAN scenario to avoid the need of entering network addresses and similar information. 

Suggestions for the GUI: 
* May use the JTextPane class to show html files.
* May use a set of jpg images (one per slide, easy to show in Java)
In general, do not spend much time in implementing the GUI. 


### Impelementation details
* It is adopted a UDP connection to enable fast data transfer.
* An adaption of the distributed bully election algorithm has been implemented for the leader election process in case of leader crash.