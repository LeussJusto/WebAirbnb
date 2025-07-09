import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Star, MapPin, Users, Bed, Bath, Wifi, Car, Coffee, Flame, Waves, Eye, ArrowLeft, Heart, Share } from 'lucide-react';
import { useAccommodations } from '../hooks/useAccommodations';
import { useAuth } from '../hooks/useAuth';
import { useApp } from '../contexts/AppContext';

const amenityIcons = {
  'WiFi': <Wifi className="h-5 w-5" />,
  'Kitchen': <Coffee className="h-5 w-5" />,
  'Fireplace': <Flame className="h-5 w-5" />,
  'Hot Tub': <Waves className="h-5 w-5" />,
  'Pool': <Waves className="h-5 w-5" />,
  'Parking': <Car className="h-5 w-5" />,
  'Ocean View': <Eye className="h-5 w-5" />,
  'Mountain View': <Eye className="h-5 w-5" />,
  'City View': <Eye className="h-5 w-5" />,
  'Forest View': <Eye className="h-5 w-5" />,
};

export default function AccommodationDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getAccommodationById } = useAccommodations();
  const { user } = useAuth();
  const { dispatch } = useApp();
  
  const [selectedImage, setSelectedImage] = useState(0);
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [guests, setGuests] = useState(1);
  const [isBooking, setIsBooking] = useState(false);

  const accommodation = getAccommodationById(id!);

  if (!accommodation) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Alojamiento no encontrado</h2>
          <button
            onClick={() => navigate('/accommodations')}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Volver
          </button>
        </div>
      </div>
    );
  }

  const handleBooking = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (!checkIn || !checkOut) {
      alert('Please select check-in and check-out dates');
      return;
    }

    setIsBooking(true);
    
    // Simulate booking process
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    const nights = Math.ceil((new Date(checkOut).getTime() - new Date(checkIn).getTime()) / (1000 * 60 * 60 * 24));
    const totalPrice = nights * accommodation.price;
    
    const booking = {
      id: Date.now().toString(),
      accommodationId: accommodation.id,
      userId: user.id,
      checkIn,
      checkOut,
      guests,
      totalPrice,
      status: 'confirmed' as const,
    };
    
    dispatch({ type: 'ADD_BOOKING', payload: booking });
    setIsBooking(false);
    navigate('/bookings');
  };

  const nights = checkIn && checkOut ? Math.ceil((new Date(checkOut).getTime() - new Date(checkIn).getTime()) / (1000 * 60 * 60 * 24)) : 0;
  const totalPrice = nights * accommodation.price;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft className="h-5 w-5" />
            <span>Volver</span>
          </button>
          
          <div className="flex items-center space-x-4">
            <button className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
              <Share className="h-5 w-5" />
              <span>Compartir</span>
            </button>
            <button className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
              <Heart className="h-5 w-5" />
              <span>Guardar</span>
            </button>
          </div>
        </div>

        {/* Title and Rating */}
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">{accommodation.title}</h1>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-1">
              <Star className="h-5 w-5 text-yellow-400 fill-current" />
              <span className="font-medium">{accommodation.rating}</span>
              <span className="text-gray-500">({accommodation.reviews} reviews)</span>
            </div>
            <div className="flex items-center space-x-1 text-gray-500">
              <MapPin className="h-5 w-5" />
              <span>{accommodation.location}</span>
            </div>
          </div>
        </div>

        {/* Images */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-8">
          <div className="lg:col-span-1">
            <img
              src={accommodation.images[selectedImage]}
              alt={accommodation.title}
              className="w-full h-96 object-cover rounded-xl"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            {accommodation.images.slice(0, 4).map((image, index) => (
              <button
                key={index}
                onClick={() => setSelectedImage(index)}
                className={`relative overflow-hidden rounded-lg ${
                  selectedImage === index ? 'ring-2 ring-blue-500' : ''
                }`}
              >
                <img
                  src={image}
                  alt={`${accommodation.title} ${index + 1}`}
                  className="w-full h-44 object-cover hover:scale-105 transition-transform duration-300"
                />
              </button>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Host Info */}
            <div className="bg-white rounded-xl p-6 shadow-sm mb-6">
              <div className="flex items-center space-x-4 mb-4">
                <img
                  src={accommodation.hostAvatar}
                  alt={accommodation.hostName}
                  className="w-12 h-12 rounded-full"
                />
                <div>
                  <h3 className="font-semibold text-gray-900">De {accommodation.hostName}</h3>
                  <p className="text-gray-500 text-sm">Superhost</p>
                </div>
              </div>
              
              <div className="flex items-center space-x-6 text-gray-600">
                <div className="flex items-center space-x-1">
                  <Users className="h-5 w-5" />
                  <span>{accommodation.maxGuests} personas</span>
                </div>
                <div className="flex items-center space-x-1">
                  <Bed className="h-5 w-5" />
                  <span>{accommodation.bedrooms} cuartos</span>
                </div>
                <div className="flex items-center space-x-1">
                  <Bath className="h-5 w-5" />
                  <span>{accommodation.bathrooms} baños</span>
                </div>
              </div>
            </div>

            {/* Description */}
            <div className="bg-white rounded-xl p-6 shadow-sm mb-6">
              <h3 className="text-xl font-semibold text-gray-900 mb-4">Acerca del lugar</h3>
              <p className="text-gray-700 leading-relaxed">{accommodation.description}</p>
            </div>

            {/* Amenities */}
            <div className="bg-white rounded-xl p-6 shadow-sm mb-6">
              <h3 className="text-xl font-semibold text-gray-900 mb-4">Qué ofrece el lugar?</h3>
              <div className="grid grid-cols-2 gap-4">
                {accommodation.amenities.map((amenity, index) => (
                  <div key={index} className="flex items-center space-x-3">
                    <div className="text-gray-600">
                      {amenityIcons[amenity as keyof typeof amenityIcons] || <Coffee className="h-5 w-5" />}
                    </div>
                    <span className="text-gray-700">{amenity}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Booking Card */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl p-6 shadow-lg sticky top-8">
              <div className="flex items-baseline space-x-1 mb-6">
                <span className="text-3xl font-bold text-gray-900">${accommodation.price}</span>
                <span className="text-gray-500">/ noche</span>
              </div>

              <div className="space-y-4 mb-6">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Entrada</label>
                    <input
                      type="date"
                      value={checkIn}
                      onChange={(e) => setCheckIn(e.target.value)}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Salida</label>
                    <input
                      type="date"
                      value={checkOut}
                      onChange={(e) => setCheckOut(e.target.value)}
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Personas</label>
                  <select
                    value={guests}
                    onChange={(e) => setGuests(Number(e.target.value))}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    {Array.from({ length: accommodation.maxGuests }, (_, i) => i + 1).map(num => (
                      <option key={num} value={num}>{num} persona{num > 1 ? 's' : ''}</option>
                    ))}
                  </select>
                </div>
              </div>

              {nights > 0 && (
                <div className="border-t border-gray-200 pt-4 mb-6">
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-gray-700">${accommodation.price} × {nights} noches</span>
                    <span className="text-gray-900">${accommodation.price * nights}</span>
                  </div>
                  <div className="flex justify-between items-center font-semibold text-lg">
                    <span>Total</span>
                    <span>${totalPrice}</span>
                  </div>
                </div>
              )}

              <button
                onClick={handleBooking}
                disabled={isBooking || !checkIn || !checkOut}
                className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
              >
                {isBooking ? 'Procesando...' : user ? 'Reservado' : 'Inicia sesión'}
              </button>

              {!user && (
                <p className="text-center text-sm text-gray-500 mt-2">
                  No se le cobrará todavía
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}