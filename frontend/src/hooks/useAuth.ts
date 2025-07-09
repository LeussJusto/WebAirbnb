import { useApp } from '../contexts/AppContext';

export function useAuth() {
  const { state, dispatch } = useApp();

  const login = async (email: string, password: string) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const mockUser = {
      id: '1',
      name: 'John Doe',
      email,
      avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=150&h=150&dpr=2',
      isHost: true,
    };
    
    dispatch({ type: 'SET_USER', payload: mockUser });
    dispatch({ type: 'SET_LOADING', payload: false });
    
    return mockUser;
  };

  const register = async (name: string, email: string, password: string) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const mockUser = {
      id: '2',
      name,
      email,
      avatar: 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=150&h=150&dpr=2',
      isHost: false,
    };
    
    dispatch({ type: 'SET_USER', payload: mockUser });
    dispatch({ type: 'SET_LOADING', payload: false });
    
    return mockUser;
  };

  const logout = () => {
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