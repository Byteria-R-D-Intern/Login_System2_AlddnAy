
(function (global) {

    function getToken() {
        return localStorage.getItem('authToken');
    }

    function setToken(token) {
        localStorage.setItem('authToken', token);
    }
    
    function parseJwt(token) {

        try{
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64)
            .split('')
            .map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            })
            .join(''));
            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error('JWT decode hatası:', error);
            return null;
        }
    }

    function getUserIdFromToken(token) {
        const payload = parseJwt(token);
        if (!payload) return null;
        return payload.id ?? payload.sub ?? null; // her iki alanı da destekle
      }
    function getRoleFromToken(token) {
        const payload = parseJwt(token);
        return payload?.role ?? null;
      }
    function isTokenExpired(token) {
        const payload = parseJwt(token);
        if (!payload?.exp) return false; 
        const nowSec = Math.floor(Date.now() / 1000);
        return payload.exp <= nowSec;
      }
    function ensureAuthenticated() {
        const token = getToken();
        if (!token) {
          window.location.href = '/login';
          return null;
        }
        if (isTokenExpired(token)) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
          return null;
        }
        const userId = Number(getUserIdFromToken(token));
        const role = getRoleFromToken(token);
        if (!userId) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
          return null;
        }
        return { token, userId, role };
      }
    function logout() {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userRole');
        window.location.href = '/login';
    }

    async function authFetch(url, options = {}) {
        const session = Auth.ensureAuthenticated();
        if (!session) return new Response(null, { status: 401 });
      
        const { token } = session;
        const headers = new Headers(options.headers || undefined);
        headers.set('Authorization', `Bearer ${token}`);
      
        const hasBody = options.body !== undefined && options.body !== null;
        if (hasBody && !headers.has('Content-Type')) {
          headers.set('Content-Type', 'application/json');
        }
      
        const response = await fetch(url, { ...options, headers });
        if (response.status === 401) {
          logout();
        }
        return response;
      }

      global.Auth = {
        getToken,
        setToken,
        parseJwt,
        getUserIdFromToken,
        getRoleFromToken,
        isTokenExpired,
        ensureAuthenticated,
        logout,
        authFetch
      };
    

})(window);