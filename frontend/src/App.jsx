import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { 
  Gem, 
  TrendingUp, 
  RefreshCw, 
  Search, 
  Shield, 
  User, 
  DollarSign, 
  Award,
  AlertTriangle
} from 'lucide-react';

function App() {
  const [gems, setGems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  const fetchTopGems = async () => {
    try {
      setError(null);
      const response = await fetch('http://localhost:8080/api/v1/analytics/top-gems');
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);
      }
      const data = await response.json();
      setGems(data);
    } catch (err) {
      console.error("Error al obtener los datos de la API de MARS-Core:", err);
      setError("No se pudo conectar con el servidor analítico de MARS-Core. Asegúrate de que el backend de Spring Boot se esté ejecutando en el puerto 8080 y la configuración de CORS esté activa.");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchTopGems();
  }, []);

  const handleRefresh = () => {
    setRefreshing(true);
    fetchTopGems();
  };

  // Formateador de moneda
  const formatCurrency = (val) => {
    if (val === null || val === undefined) return "$0";
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0
    }).format(val);
  };

  // Color de etiqueta según IEM
  const getIemColorClass = (iem) => {
    if (iem >= 8.5) return 'text-info';
    if (iem >= 7.0) return 'text-success';
    return 'text-warning';
  };

  return (
    <div className="container-fluid py-5 px-md-5" style={{ minHeight: '100vh' }}>
      
      {/* Header Analítico */}
      <header className="d-flex flex-column flex-md-row justify-content-between align-items-center mb-5 pb-4 border-bottom border-secondary">
        <div className="d-flex align-items-center gap-3 mb-3 mb-md-0">
          <div className="p-3 bg-dark rounded-circle border border-primary d-flex align-items-center justify-content-center" style={{ boxShadow: '0 0 15px rgba(138, 43, 226, 0.4)' }}>
            <Gem size={32} className="text-info" />
          </div>
          <div>
            <h1 className="h2 m-0 fw-bold tracking-tight text-white" style={{ fontFamily: "'Space Grotesk', sans-serif" }}>
              MARS-CORE <span className="text-info">//</span> ANALYTICS HUB
            </h1>
            <p className="text-muted small m-0">Plataforma Avanzada de Scouting Deportivo y Modelado Moneyball</p>
          </div>
        </div>
        <div>
          <button 
            className="btn btn-outline-info d-flex align-items-center gap-2 px-4 py-2"
            onClick={handleRefresh}
            disabled={loading || refreshing}
            style={{ borderRadius: '10px', transition: 'all 0.2s' }}
          >
            <RefreshCw size={18} className={refreshing ? 'spin-animation' : ''} />
            {refreshing ? 'Actualizando...' : 'Recargar Datos'}
          </button>
        </div>
      </header>

      {/* Alerta de Error */}
      {error && (
        <div className="alert bg-dark text-white border border-danger p-4 mb-5 rounded-4 d-flex align-items-start gap-3 shadow-lg">
          <AlertTriangle size={32} className="text-danger flex-shrink-0" />
          <div>
            <h5 className="fw-bold text-danger m-0 mb-1">Fallo de Conexión Analítica</h5>
            <p className="m-0 text-muted small">{error}</p>
          </div>
        </div>
      )}

      {/* Contenido Principal */}
      {loading ? (
        <div className="d-flex flex-column align-items-center justify-content-center my-5 py-5 text-center">
          <div className="spinner-border text-info mb-3" role="status" style={{ width: '3rem', height: '3rem' }}>
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p className="text-muted">Procesando matriz de dominancia y sincronizando joyas ocultas...</p>
        </div>
      ) : (
        <>
          {/* Ficha Resumen de Joyas */}
          <div className="row g-4 mb-5">
            <div className="col-12 col-md-4">
              <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
                <div>
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <span className="text-muted small uppercase fw-bold tracking-wider">Joyas Totales</span>
                    <Award className="text-info" size={24} />
                  </div>
                  <h2 className="display-6 fw-bold text-white mb-2">{gems.length}</h2>
                  <p className="text-muted small">Candidatos en el Top 5 que superan el umbral mínimo del IEM analítico.</p>
                </div>
                <div className="mt-3 pt-3 border-top border-secondary">
                  <span className="text-info small fw-bold">✓ Algoritmo Ponderado Activo</span>
                </div>
              </div>
            </div>
            
            <div className="col-12 col-md-4">
              <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
                <div>
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <span className="text-muted small uppercase fw-bold tracking-wider">Eficiencia Promedio</span>
                    <TrendingUp className="text-info" size={24} />
                  </div>
                  <h2 className="display-6 fw-bold text-white mb-2">
                    {gems.length > 0 
                      ? (gems.reduce((acc, curr) => acc + curr.iem, 0) / gems.length).toFixed(2) 
                      : "0.00"
                    }
                  </h2>
                  <p className="text-muted small">Rendimiento deportivo general escalado en base a costes financieros del mercado.</p>
                </div>
                <div className="mt-3 pt-3 border-top border-secondary">
                  <span className="text-info small fw-bold">Escala normalizada [0.0 - 10.0]</span>
                </div>
              </div>
            </div>

            <div className="col-12 col-md-4">
              <div className="glass-card p-4 h-100 d-flex flex-column justify-content-between">
                <div>
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <span className="text-muted small uppercase fw-bold tracking-wider">Búsquedas Totales</span>
                    <Search className="text-info" size={24} />
                  </div>
                  <h2 className="display-6 fw-bold text-white mb-2">
                    {gems.reduce((acc, curr) => acc + curr.busquedas, 0)}
                  </h2>
                  <p className="text-muted small">Cantidad de simulaciones de scouting realizadas por directores técnicos.</p>
                </div>
                <div className="mt-3 pt-3 border-top border-secondary">
                  <span className="text-info small fw-bold">Clasificación en tiempo real</span>
                </div>
              </div>
            </div>
          </div>

          {/* Grid de Tarjetas de Joyas */}
          <h3 className="fw-bold mb-4 text-white d-flex align-items-center gap-2" style={{ fontFamily: "'Space Grotesk', sans-serif" }}>
            <Gem className="text-info" /> TOP 5 JOYAS DETECTADAS (MONEYBALL)
          </h3>
          
          <div className="row g-4 mb-5">
            {gems.map((gem, idx) => (
              <div key={gem.id} className="col-12 col-md-6 col-lg-4">
                <div className="glass-card p-4 position-relative overflow-hidden h-100 d-flex flex-column justify-content-between">
                  {/* Puesto en el Top */}
                  <div className="position-absolute top-0 end-0 bg-dark border-bottom border-start border-secondary px-3 py-1 rounded-bl-4 text-info fw-bold" style={{ fontSize: '0.9rem' }}>
                    Rank #{idx + 1}
                  </div>
                  
                  <div>
                    {/* Nombre y posición */}
                    <div className="mb-3">
                      <h4 className="fw-bold text-white m-0 text-truncate pe-5">{gem.nombre}</h4>
                      <span className="badge bg-secondary text-info text-uppercase mt-1" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                        {gem.posicion}
                      </span>
                    </div>

                    {/* Info Técnica */}
                    <div className="d-flex flex-column gap-2 mb-4">
                      <div className="d-flex align-items-center gap-2 text-muted small">
                        <Shield size={16} className="text-info" />
                        <span className="text-truncate">Club: <strong>{gem.club}</strong></span>
                      </div>
                      <div className="d-flex align-items-center gap-2 text-muted small">
                        <DollarSign size={16} className="text-info" />
                        <span>Valor: <strong>{formatCurrency(gem.costo)}</strong></span>
                      </div>
                      <div className="d-flex align-items-center gap-2 text-muted small">
                        <Search size={16} className="text-info" />
                        <span>Simulaciones: <strong>{gem.busquedas} búsquedas</strong></span>
                      </div>
                    </div>
                  </div>

                  {/* Panel de IEM */}
                  <div className="bg-dark p-3 rounded-3 d-flex justify-content-between align-items-center border border-secondary">
                    <span className="small text-muted fw-bold">MARS-IEM</span>
                    <div className="iem-badge">
                      {gem.iem.toFixed(2)}
                    </div>
                  </div>

                </div>
              </div>
            ))}

            {gems.length === 0 && !error && (
              <div className="col-12 text-center py-5 glass-card">
                <p className="text-muted">No hay joyas registradas en el sistema actualmente.</p>
                <p className="text-muted small">Realiza búsquedas en la plataforma principal de Spring Boot para detectar talentos con alto IEM.</p>
              </div>
            )}
          </div>

          {/* Tabla Analítica */}
          {gems.length > 0 && (
            <div className="glass-card p-4 overflow-hidden mb-5">
              <h4 className="fw-bold text-white mb-4" style={{ fontFamily: "'Space Grotesk', sans-serif" }}>MATRIZ DE EFICIENCIA COMPARADA</h4>
              <div className="table-responsive">
                <table className="table custom-table w-100">
                  <thead>
                    <tr>
                      <th scope="col">Ránking</th>
                      <th scope="col">Jugador</th>
                      <th scope="col">Posición</th>
                      <th scope="col">Club Actual</th>
                      <th scope="col" className="text-end">Costo Financiero</th>
                      <th scope="col" className="text-center">Índice IEM</th>
                      <th scope="col" className="text-center">Búsquedas</th>
                    </tr>
                  </thead>
                  <tbody>
                    {gems.map((gem, idx) => (
                      <tr key={gem.id}>
                        <td>
                          <span className="fw-bold text-info">#{idx + 1}</span>
                        </td>
                        <td>
                          <div className="d-flex align-items-center gap-2">
                            <div className="p-2 bg-dark rounded-circle border border-secondary d-flex align-items-center justify-content-center">
                              <User size={16} className="text-info" />
                            </div>
                            <span className="fw-bold text-white">{gem.nombre}</span>
                          </div>
                        </td>
                        <td>
                          <span className="text-uppercase small fw-bold text-muted">{gem.posicion}</span>
                        </td>
                        <td>
                          <span>{gem.club}</span>
                        </td>
                        <td className="text-end fw-bold text-white">
                          {formatCurrency(gem.costo)}
                        </td>
                        <td className="text-center">
                          <span className={`fw-bold ${getIemColorClass(gem.iem)}`}>
                            {gem.iem.toFixed(2)}
                          </span>
                        </td>
                        <td className="text-center text-muted">
                          {gem.busquedas}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}

      {/* Footer */}
      <footer className="text-center text-muted small mt-5 pt-4 border-top border-secondary">
        <p className="m-0">© {new Date().getFullYear()} MARS-Core Analytics. Desarrollado bajo la metodología Moneyball y normalización logarítmica.</p>
        <p className="m-0 mt-1" style={{ fontSize: '0.75rem' }}>Efecto Lejeune y matrices de dominancia competitiva integradas en tiempo de ejecución.</p>
      </footer>

      {/* Animaciones CSS adicionales */}
      <style>{`
        .spin-animation {
          animation: spin 1s linear infinite;
        }
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>

    </div>
  );
}

export default App;
