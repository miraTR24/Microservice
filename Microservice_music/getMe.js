const fs = require('fs')
const SpotifyWebApi = require('spotify-web-api-node');
const token = "BQBnYK9GYWvC59H8kpJrhfAz0AOxgEEmen0Gam3vk0Jxm5YaTY_xDNZ0rsw-KHL3-hASWqPVEv2XMxfsd9o6qvJ4bubESYstvPT8HSMfWI92AmGxbQLLsi7nuupTTtRfnLGpO4zViQ3FKlf2PauCvIgSybByUGpI8-cIb5MSqJ9jmZowrg8cPE5ngr7k5iGvyJcKFlCwMw_vQYqETLVCcSS4ef7PrTdgc4FvuYsyQR7aPMFjWaQvuG4y7zp3JoaO4X7OiPpd4jFEh0Wpj8ukI7NmHki04wzFym7ZD7w-YzNtg9uxNqZFfRVU0A4QHd1U4G1HZ_DT7YHBn1LU1xEz";

const spotifyApi = new SpotifyWebApi();
spotifyApi.setAccessToken(token);

//GET MY PROFILE DATA
function getMyData() {
  (async () => {
    const me = await spotifyApi.getMe();
    // console.log(me.body);
    getUserPlaylists(me.body.id);
  })().catch(e => {
    console.error(e);
  });
}

//GET MY PLAYLISTS
async function getUserPlaylists(userName) {
  const data = await spotifyApi.getUserPlaylists(userName)

  console.log("---------------+++++++++++++++++++++++++")
  let playlists = []

  for (let playlist of data.body.items) {
    console.log(playlist.name + " " + playlist.id)
    
    let tracks = await getPlaylistTracks(playlist.id, playlist.name);
    // console.log(tracks);

    const tracksJSON = { tracks }
    let data = JSON.stringify(tracksJSON);
    fs.writeFileSync(playlist.name+'.json', data);
  }
}

//GET SONGS FROM PLAYLIST
async function getPlaylistTracks(playlistId, playlistName) {

  const data = await spotifyApi.getPlaylistTracks(playlistId, {
    offset: 1,
    limit: 100,
    fields: 'items'
  })

  // console.log('The playlist contains these tracks', data.body);
  // console.log('The playlist contains these tracks: ', data.body.items[0].track);
  // console.log("'" + playlistName + "'" + ' contains these tracks:');
  let tracks = [];

  for (let track_obj of data.body.items) {
    const track = track_obj.track
    tracks.push(track);
    console.log(track.name + " : " + track.artists[0].name)
  }
  
  console.log("---------------+++++++++++++++++++++++++")
  return tracks;
}

getMyData();