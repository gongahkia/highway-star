from diagrams import Diagram, Cluster, Edge
from diagrams.custom import Custom
from diagrams.onprem.compute import Server
from diagrams.onprem.client import User

with Diagram("Highway Star Architecture", show=False, 
            filename="highway_star_arch", 
            direction="LR",
            graph_attr={"splines": "ortho", "nodesep": "1.0", "ranksep": "1.5"}):
    with Cluster("Client Side"):
        user = User("User")
        frontend = Custom("Java Swing", "./java.png")
        user >> Edge(label="UI interactions", color="black") >> frontend
    with Cluster("Server Side"):
        backend = Server("Backend")
        frontend >> Edge(label="API calls", color="blue4") >> backend
    with Cluster("External Services"):
        with Cluster("Mapping Services"):
            geolocation = Custom("IP Geolocation", "./geolocation.png")
            osm = Custom("OpenStreetMap\nAPI", "./osm.png")
            geolocation >> Edge(label="Fetch context", style="dashed") >> osm
        with Cluster("Database"):
            firebase = Custom("Firebase\nRealtime\nDB", "./firebase.png")
    backend >> Edge(label="1. Client IP lookup", color="red") >> geolocation
    backend >> Edge(label="2. Map data request", color="darkgreen") >> osm
    backend >> Edge(label="3a. Store auth tokens\n3b. Activity logs", color="purple") >> firebase
    osm >> Edge(label="4. Cache metadata", style="dotted") >> firebase