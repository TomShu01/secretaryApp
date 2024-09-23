import React, { useState } from 'react'
import Tabs from '../components/tabs'
import Menu from "../components/menu"
import View from "../components/view"
import HintsBar from "../components/hintsBar"
import { makeStyles } from '@mui/styles';

export const Context = React.createContext()

const useStyles = makeStyles({
  background: {
    position: 'fixed',
    zIndex: -1,
    width: '100%',
    height: '100%',
    backgroundImage: 'url(https://t3.ftcdn.net/jpg/05/70/44/36/360_F_570443670_N4FiFcnUjrGqVq8pT8BxYPbek4nWEePz.jpg)',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    filter: 'brightness(0.9)',
    marginTop: '-8px',
    marginLeft: '-8px'
  },
})
// another cool background picture
// https://cdn.dribbble.com/users/1454037/screenshots/5360175/apartments-animated-dribbler-bottom.gif

const Main = () => {
  const classes = useStyles();
  const [currentView, setCurrentView] = useState(['List', [{ functionName: 'Search', params: { searchString: '' } },
    { functionName: 'Optimize', params: { startDate: '1900-01-01', endDate: '2099-01-01' } }]])
  const [hints, setHints] = useState([{action:'add task', bind: 'Press A to add'}, {action:'delete task', bind: 'Press ESC to exit'}])

  return (
    <div className={classes.background}>
    <Context.Provider value={[currentView, setCurrentView, hints, setHints]}>
      <Menu/>
      <Tabs/>
      <View/>
      <HintsBar hints = {hints} />
    </Context.Provider>
    </div>
  )
}

export default Main