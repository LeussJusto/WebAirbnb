import axios from 'axios';
import { useApp } from '../contexts/AppContext';

const API_URL = 'http://localhost:8080/api/auth';

export function useAuth() {
  const { state, dispatch } = useApp();

  const login = async (email: string, password: string) => {
    dispatch({ type: 'SET_LOADING', payload: true });

    try {
      const response = await axios.post(`${API_URL}/login`, { email, password });
      const { token, user } = response.data;
      localStorage.setItem('token', token);
      dispatch({ type: 'SET_USER', payload: user });
      return user;
    } catch (error) {
      console.error('Error al iniciar sesión:', error);
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  };

  const register = async (name: string, email: string, password: string) => {
    dispatch({ type: 'SET_LOADING', payload: true });

    try {
      const response = await axios.post(`${API_URL}/register`, { name, email, password });
      const { token, user } = response.data;
      localStorage.setItem('token', token);
      dispatch({ type: 'SET_USER', payload: user });
      return user;
    } catch (error) {
      console.error('Error al registrarse:', error);
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    dispatch({ type: 'SET_USER', payload: null });
  };

  return {
    user: state.user,
    isLoading: state.isLoading,
    login,
    register,
    logout,
  };
}
