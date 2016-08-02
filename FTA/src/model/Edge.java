package model;

public class Edge {

   private int source_id;
   private int destination_id;
   
   public Edge(){   }
   public Edge(int source_id, int destination_id){
      
   }
   public Edge(int source_id, String source_name){
      this.source_id = source_id;
   }
   public int getDestination_id() {
      return destination_id;
   }
   public void setDestination_id(int destination_id) {
      this.destination_id = destination_id;
   }
   public int getSourceID(){
      return this.source_id;
   }
   public void setSourceID(int source_id){
      this.source_id = source_id;
   }
}