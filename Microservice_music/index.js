const express = require('express');
const SpotifyWebApi = require('spotify-web-api-node');
const querystring = require('querystring');
const axios = require('axios');


const app = express();
app.use(express.static('public')); // Ajout de cette ligne pour servir les fichiers statiques


const client_id = 'ad765c37091e4372b99a86b16bc57657';
const client_secret = '3c899ec39b904229a7a98cb036841c14';
const redirect_uri = 'http://localhost:8888/callback';


const spotifyApi = new SpotifyWebApi({
    clientId: client_id,
    clientSecret: client_secret,
    redirectUri: redirect_uri
});


function generateRandomString(length) {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}


app.get('/login', function(req, res) {
    if (spotifyApi.getAccessToken()) {
        res.redirect('/confirm.html');
    } else {
        const state = generateRandomString(16);
        const scope = 'user-read-private user-read-email playlist-read-private playlist-read-collaborative';


        res.redirect('https://accounts.spotify.com/authorize?' +
            querystring.stringify({
                response_type: 'code',
                client_id: client_id,
                scope: scope,
                redirect_uri: redirect_uri,
                state: state
            }));
    }
});




app.get('/callback', async function(req, res) {
    const code = req.query.code || null;
    const state = req.query.state || null;


    if (state === null) {
        res.redirect('/error.html?' +
            querystring.stringify({
                error: 'state_mismatch'
            }));
    } else {
        try {
            const authOptions = {
                method: 'post',
                url: 'https://accounts.spotify.com/api/token',
                data: querystring.stringify({
                    code: code,
                    redirect_uri: redirect_uri,
                    grant_type: 'authorization_code'
                }),
                headers: {
                    'content-type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Basic ' + (Buffer.from(client_id + ':' + client_secret).toString('base64'))
                }
            };


            const response = await axios(authOptions);
            const body = response.data;


            const access_token = body.access_token;
            const refresh_token = body.refresh_token;


            spotifyApi.setAccessToken(access_token);
            spotifyApi.setRefreshToken(refresh_token);


            res.redirect('/success.html');
        } catch (error) {
            console.error('Error getting Tokens:', error);
            res.redirect('/error.html');
        }
    }
});


app.get('/playlists', async (req, res) => {
  try {

      console.log("Access token ", spotifyApi.getAccessToken());

      // Accéder aux playlists si le jeton d'accès est disponible
      const data = await spotifyApi.getUserPlaylists();
      res.json(data.body);
      console.log(res.json(data.body));
  } catch (error) {
      console.error('Error fetching playlists:', error);
      res.status(500).send('Error fetching playlists');
  }
});





app.listen(8888, () =>
    console.log(
        'Serveur HTTP démarré. Allez sur http://localhost:8888/login dans votre navigateur.'
    )
);



