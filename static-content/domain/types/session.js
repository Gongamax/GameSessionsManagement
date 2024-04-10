export function Session(
  sid, numberOfPlayers, date, gid, associatedPlayers, capacity,
) {
  this.sid = sid;
  this.numberOfPlayers = numberOfPlayers;
  this.date = date;
  this.gid = gid;
  this.associatedPlayers = associatedPlayers;
  this.capacity = capacity;
}

