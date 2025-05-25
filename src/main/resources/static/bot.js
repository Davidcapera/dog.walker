
let myLandbot;

function initLandbot() {
  if (!myLandbot) {
    const s = document.createElement('script');
    s.type = 'module';
    s.async = true;

    s.addEventListener('load', () => {
      // Asignar correctamente a la variable global
      myLandbot = new Landbot.Livechat({
        configUrl: 'https://storage.googleapis.com/landbot.online/v3/H-2949214-32VPZ4J8P2Q3YT0E/index.json',
      });
    });

    s.src = 'https://cdn.landbot.io/landbot-3/landbot-3.0.0.mjs';
    const firstScript = document.getElementsByTagName('script')[0];
    firstScript.parentNode.insertBefore(s, firstScript);
  }
}

// Solo inicializa una vez por interacción del usuario
window.addEventListener('mouseover', initLandbot, { once: true });
window.addEventListener('touchstart', initLandbot, { once: true });